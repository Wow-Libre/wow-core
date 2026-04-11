package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.social.*;
import com.register.wowlibre.domain.port.in.social.SocialPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.domain.shared.GenericResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.register.wowlibre.domain.constant.Constants.HEADER_TRANSACTION_ID;
import static com.register.wowlibre.domain.constant.Constants.HEADER_USER_ID;

@RestController
@RequestMapping("/api/social")
public class SocialController {

    private final SocialPort socialPort;

    public SocialController(SocialPort socialPort) {
        this.socialPort = socialPort;
    }

    @GetMapping("/posts")
    public ResponseEntity<GenericResponse<List<SocialPostDto>>> listPosts(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        List<SocialPostDto> data = socialPort.listPosts(page, size, userId, transactionId);
        return ResponseEntity.ok(new GenericResponseBuilder<>(data, transactionId).ok().build());
    }

    @PostMapping("/posts")
    public ResponseEntity<GenericResponse<SocialPostDto>> createPost(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long userId,
            @Valid @RequestBody CreatePostRequestDto request) {
        SocialPostDto created = socialPort.createPost(userId, request, transactionId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<>(created, transactionId).created().build());
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<GenericResponse<LikeToggleResponseDto>> toggleLike(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long userId,
            @PathVariable Long postId) {
        LikeToggleResponseDto data = socialPort.toggleLike(postId, userId, transactionId);
        return ResponseEntity.ok(new GenericResponseBuilder<>(data, transactionId).ok().build());
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<GenericResponse<List<SocialCommentDto>>> listComments(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @PathVariable Long postId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        List<SocialCommentDto> data = socialPort.listComments(postId, page, size, transactionId);
        return ResponseEntity.ok(new GenericResponseBuilder<>(data, transactionId).ok().build());
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<GenericResponse<SocialCommentDto>> createComment(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequestDto request) {
        SocialCommentDto created = socialPort.createComment(postId, userId, request, transactionId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<>(created, transactionId).created().build());
    }

    @PostMapping("/media/presign")
    public ResponseEntity<GenericResponse<PresignResponseDto>> presign(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long userId,
            @Valid @RequestBody PresignRequestDto request) {
        PresignResponseDto data = socialPort.presignMedia(userId, request, transactionId);
        return ResponseEntity.ok(new GenericResponseBuilder<>(data, transactionId).ok().build());
    }
}
