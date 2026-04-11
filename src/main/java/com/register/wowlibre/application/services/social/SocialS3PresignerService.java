package com.register.wowlibre.application.services.social;

import com.register.wowlibre.domain.dto.social.*;
import com.register.wowlibre.domain.exception.*;
import org.apache.commons.lang3.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.*;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.*;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.time.*;
import java.util.*;

@Service
public class SocialS3PresignerService {

    private static final int PRESIGN_SECONDS = 300;

    @Value("${social.s3.bucket:}")
    private String bucket;

    @Value("${social.s3.region:us-east-1}")
    private String region;

    @Value("${social.s3.access-key:}")
    private String accessKey;

    @Value("${social.s3.secret-key:}")
    private String secretKey;

    @Value("${social.media.max-bytes:10485760}")
    private long maxBytes;

    @Value("${social.media.allowed-content-types:image/jpeg,image/png,image/webp,image/gif,video/mp4}")
    private String allowedContentTypes;

    private static String sanitizeFilename(String name) {
        if (name == null || name.isBlank()) {
            return "file.bin";
        }
        String base = name.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (base.length() > 180) {
            base = base.substring(0, 180);
        }
        return base.isEmpty() ? "file.bin" : base;
    }

    public PresignResponseDto presign(Long userId, PresignRequestDto request, String transactionId) {
        if (StringUtils.isBlank(bucket)) {
            throw new GenericErrorException(transactionId, "Social media uploads are not configured (SOCIAL_S3_BUCKET)",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (request.getByteSize() == null || request.getByteSize() > maxBytes) {
            throw new GenericErrorException(transactionId, "File size not allowed", HttpStatus.PAYLOAD_TOO_LARGE);
        }
        String contentType = request.getContentType().trim().toLowerCase(Locale.ROOT);
        Set<String> allowed = Set.of(allowedContentTypes.split(","));
        if (!allowed.contains(contentType)) {
            throw new GenericErrorException(transactionId, "Content type not allowed: " + contentType,
                    HttpStatus.BAD_REQUEST);
        }

        String safeName = sanitizeFilename(request.getFilename());


        AwsCredentialsProvider credentialsProvider = resolveCredentials();

        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(bucket)
                .key(safeName)
                .contentType(contentType)
                .contentLength(request.getByteSize())
                .build();

        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build()) {

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(PRESIGN_SECONDS))
                    .putObjectRequest(put)
                    .build();

            PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);
            String uploadUrl = presigned.url().toString();
            String publicUrl = buildPublicUrl(safeName);

            return PresignResponseDto.builder()
                    .uploadUrl(uploadUrl)
                    .publicUrl(publicUrl)
                    .key(safeName)
                    .expiresInSeconds(PRESIGN_SECONDS)
                    .build();
        }
    }

    private AwsCredentialsProvider resolveCredentials() {
        if (StringUtils.isNotBlank(accessKey) && StringUtils.isNotBlank(secretKey)) {
            return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey.trim(), secretKey.trim()));
        }
        return DefaultCredentialsProvider.create();
    }

    private String buildPublicUrl(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }
}
