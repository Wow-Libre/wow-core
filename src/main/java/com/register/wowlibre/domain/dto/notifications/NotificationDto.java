package com.register.wowlibre.domain.dto.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String title;
    private String message;

    @JsonProperty("read")
    private boolean read;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
