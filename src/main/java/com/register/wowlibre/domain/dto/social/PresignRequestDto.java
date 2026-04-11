package com.register.wowlibre.domain.dto.social;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresignRequestDto {

    @NotBlank
    private String filename;

    @NotBlank
    private String contentType;

    @Min(1)
    @NotNull
    private Long byteSize;
}
