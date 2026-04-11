package com.register.wowlibre.application.services.social;

import com.register.wowlibre.domain.dto.social.*;
import com.register.wowlibre.domain.exception.GenericErrorException;
import com.register.wowlibre.domain.port.in.social.SocialPort;
import com.register.wowlibre.domain.port.in.subscriptions.SubscriptionPort;
import com.register.wowlibre.domain.port.out.notifications.ManageNotification;
import com.register.wowlibre.infrastructure.entities.NotificationEntity;
import com.register.wowlibre.infrastructure.entities.UserEntity;
import com.register.wowlibre.infrastructure.entities.social.*;
import com.register.wowlibre.infrastructure.repositories.social.*;
import com.register.wowlibre.infrastructure.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialFeedService implements SocialPort {

    private static final int MAX_MEDIA_URLS = 10;

    /** Máximo de publicaciones en ventana móvil de 24 h para usuarios sin suscripción activa. */
    private static final int FREE_TIER_MAX_POSTS_PER_24H = 5;

    private final SocialPostRepository socialPostRepository;
    private final SocialPostMediaRepository socialPostMediaRepository;
    private final SocialPostLikeRepository socialPostLikeRepository;
    private final SocialPostCommentRepository socialPostCommentRepository;
    private final UserRepository userRepository;
    private final SocialS3PresignerService socialS3PresignerService;
    private final ManageNotification manageNotification;
    private final SubscriptionPort subscriptionPort;

    @Override
    @Transactional(readOnly = true)
    public List<SocialPostDto> listPosts(int page, int size, Long currentUserId, String transactionId) {
        Page<SocialPostEntity> result = socialPostRepository.findByDeletedAtIsNullOrderByCreatedAtDesc(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<SocialPostEntity> posts = result.getContent();
        if (posts.isEmpty()) {
            return List.of();
        }
        Set<Long> userIds = posts.stream().map(SocialPostEntity::getUserId).collect(Collectors.toSet());
        Map<Long, UserEntity> users = new HashMap<>();
        userRepository.findAllById(userIds).forEach(u -> users.put(u.getId(), u));
        Map<Long, Boolean> premiumByUserId = new HashMap<>();
        for (Long authorId : userIds) {
            premiumByUserId.put(authorId, subscriptionPort.isActiveSubscription(authorId, transactionId));
        }

        List<SocialPostDto> out = new ArrayList<>();
        for (SocialPostEntity post : posts) {
            UserEntity author = users.get(post.getUserId());
            boolean authorPremium = premiumByUserId.getOrDefault(post.getUserId(), false);
            out.add(toPostDto(post, author, currentUserId, authorPremium));
        }
        return out;
    }

    @Override
    @Transactional
    public SocialPostDto createPost(Long userId, CreatePostRequestDto request, String transactionId) {
        List<String> mediaUrls = request.getMediaUrls() != null ? request.getMediaUrls() : List.of();
        if (mediaUrls.size() > MAX_MEDIA_URLS) {
            throw new GenericErrorException(transactionId, "Too many media URLs", HttpStatus.BAD_REQUEST);
        }
        String trimmed = request.getContent() == null ? "" : request.getContent().trim();
        if (trimmed.isEmpty() && mediaUrls.isEmpty()) {
            throw new GenericErrorException(transactionId, "Content or media is required", HttpStatus.BAD_REQUEST);
        }
        String contentToStore = trimmed.isEmpty() ? " " : trimmed;
        for (String url : mediaUrls) {
            if (url == null || !url.startsWith("http")) {
                throw new GenericErrorException(transactionId, "Invalid media URL", HttpStatus.BAD_REQUEST);
            }
        }

        if (!subscriptionPort.isActiveSubscription(userId, transactionId)) {
            LocalDateTime since = LocalDateTime.now().minus(24, ChronoUnit.HOURS);
            long recentCount = socialPostRepository.countByUserIdAndDeletedAtIsNullAndCreatedAtGreaterThanEqual(userId, since);
            if (recentCount >= FREE_TIER_MAX_POSTS_PER_24H) {
                throw new GenericErrorException(
                        transactionId,
                        "Daily post limit reached (5 per 24h). Premium subscribers have unlimited posts.",
                        HttpStatus.TOO_MANY_REQUESTS);
            }
        }

        SocialPostEntity post = SocialPostEntity.builder()
                .userId(userId)
                .content(contentToStore)
                .build();
        SocialPostEntity saved = socialPostRepository.save(post);

        int order = 0;
        for (String url : mediaUrls) {
            socialPostMediaRepository.save(SocialPostMediaEntity.builder()
                    .postId(saved.getId())
                    .url(url)
                    .sortOrder(order++)
                    .build());
        }

        UserEntity author = userRepository.findById(userId)
                .orElseThrow(() -> new GenericErrorException(transactionId, "User not found", HttpStatus.NOT_FOUND));
        boolean authorPremium = subscriptionPort.isActiveSubscription(userId, transactionId);
        return toPostDto(saved, author, userId, authorPremium);
    }

    @Override
    @Transactional
    public LikeToggleResponseDto toggleLike(Long postId, Long userId, String transactionId) {
        SocialPostEntity post = socialPostRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new GenericErrorException(transactionId, "Post not found", HttpStatus.NOT_FOUND));

        Optional<SocialPostLikeEntity> existing = socialPostLikeRepository.findByPostIdAndUserId(post.getId(), userId);
        if (existing.isPresent()) {
            socialPostLikeRepository.delete(existing.get());
            return LikeToggleResponseDto.builder()
                    .liked(false)
                    .likesCount(socialPostLikeRepository.countByPostId(post.getId()))
                    .build();
        }
        socialPostLikeRepository.save(SocialPostLikeEntity.builder()
                .postId(post.getId())
                .userId(userId)
                .build());
        notifyPostAuthorOfLike(post, userId, transactionId);
        return LikeToggleResponseDto.builder()
                .liked(true)
                .likesCount(socialPostLikeRepository.countByPostId(post.getId()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocialCommentDto> listComments(Long postId, int page, int size, String transactionId) {
        if (socialPostRepository.findByIdAndDeletedAtIsNull(postId).isEmpty()) {
            throw new GenericErrorException(transactionId, "Post not found", HttpStatus.NOT_FOUND);
        }
        Page<SocialPostCommentEntity> result = socialPostCommentRepository.findByPostIdAndDeletedAtIsNullOrderByCreatedAtAsc(
                postId, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt")));
        List<SocialPostCommentEntity> comments = result.getContent();
        if (comments.isEmpty()) {
            return List.of();
        }
        Set<Long> userIds = comments.stream().map(SocialPostCommentEntity::getUserId).collect(Collectors.toSet());
        Map<Long, UserEntity> users = new HashMap<>();
        userRepository.findAllById(userIds).forEach(u -> users.put(u.getId(), u));

        List<SocialCommentDto> out = new ArrayList<>();
        for (SocialPostCommentEntity c : comments) {
            UserEntity u = users.get(c.getUserId());
            out.add(toCommentDto(c, u));
        }
        return out;
    }

    @Override
    @Transactional
    public SocialCommentDto createComment(Long postId, Long userId, CreateCommentRequestDto request, String transactionId) {
        SocialPostEntity post = socialPostRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new GenericErrorException(transactionId, "Post not found", HttpStatus.NOT_FOUND));
        UserEntity author = userRepository.findById(userId)
                .orElseThrow(() -> new GenericErrorException(transactionId, "User not found", HttpStatus.NOT_FOUND));

        SocialPostCommentEntity entity = SocialPostCommentEntity.builder()
                .postId(post.getId())
                .userId(userId)
                .content(request.getContent().trim())
                .build();
        SocialPostCommentEntity saved = socialPostCommentRepository.save(entity);
        return toCommentDto(saved, author);
    }

    @Override
    public PresignResponseDto presignMedia(Long userId, PresignRequestDto request, String transactionId) {
        return socialS3PresignerService.presign(userId, request, transactionId);
    }

    private SocialPostDto toPostDto(SocialPostEntity post, UserEntity author, Long currentUserId, boolean authorPremium) {
        List<String> media = socialPostMediaRepository.findByPostIdOrderBySortOrderAsc(post.getId()).stream()
                .map(SocialPostMediaEntity::getUrl)
                .toList();
        long likes = socialPostLikeRepository.countByPostId(post.getId());
        long comments = socialPostCommentRepository.countByPostIdAndDeletedAtIsNull(post.getId());
        boolean liked = currentUserId != null && socialPostLikeRepository.existsByPostIdAndUserId(post.getId(), currentUserId);

        return SocialPostDto.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .authorUsername(buildDisplayName(author))
                .authorAvatar(author != null ? author.getAvatarUrl() : null)
                .authorPremium(authorPremium)
                .content(post.getContent())
                .mediaUrls(media)
                .likesCount(likes)
                .commentsCount(comments)
                .likedByMe(liked)
                .createdAt(post.getCreatedAt())
                .build();
    }

    private SocialCommentDto toCommentDto(SocialPostCommentEntity c, UserEntity author) {
        return SocialCommentDto.builder()
                .id(c.getId())
                .userId(c.getUserId())
                .authorUsername(buildDisplayName(author))
                .authorAvatar(author != null ? author.getAvatarUrl() : null)
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .build();
    }

    private void notifyPostAuthorOfLike(SocialPostEntity post, Long likerUserId, String transactionId) {
        Long authorId = post.getUserId();
        if (authorId.equals(likerUserId)) {
            return;
        }
        UserEntity liker = userRepository.findById(likerUserId).orElse(null);
        String likerName = buildDisplayName(liker);
        NotificationEntity n = new NotificationEntity();
        n.setTitle("Me gusta en tu publicación");
        n.setMessage(likerName + " reaccionó a tu publicación (#" + post.getId() + ").");
        n.setRecipientUserId(authorId);
        manageNotification.save(n, transactionId);
    }

    private static String buildDisplayName(UserEntity u) {
        if (u == null) {
            return "user";
        }
        String fn = StringUtils.defaultString(u.getFirstName()).trim();
        String ln = StringUtils.defaultString(u.getLastName()).trim();
        String full = (fn + " " + ln).trim();
        if (!full.isEmpty()) {
            return full;
        }
        String email = u.getEmail();
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf('@'));
        }
        return "user-" + u.getId();
    }
}
