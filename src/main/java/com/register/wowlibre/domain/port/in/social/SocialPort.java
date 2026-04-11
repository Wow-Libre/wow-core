package com.register.wowlibre.domain.port.in.social;

import com.register.wowlibre.domain.dto.social.*;

import java.util.List;

public interface SocialPort {

    List<SocialPostDto> listPosts(int page, int size, Long currentUserId, String transactionId);

    SocialPostDto createPost(Long userId, CreatePostRequestDto request, String transactionId);

    LikeToggleResponseDto toggleLike(Long postId, Long userId, String transactionId);

    List<SocialCommentDto> listComments(Long postId, int page, int size, String transactionId);

    SocialCommentDto createComment(Long postId, Long userId, CreateCommentRequestDto request, String transactionId);

    PresignResponseDto presignMedia(Long userId, PresignRequestDto request, String transactionId);
}
