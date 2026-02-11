package com.register.wowlibre.domain.dto.notifications;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {

    private Long id;

    @NotNull(message = "title is required")
    @Size(min = 1, max = 500)
    private String title;

    @Size(max = 5000)
    private String message;
}
