package com.register.wowlibre.domain.dto.social;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentRequestDto {

    @NotBlank
    @Size(max = 2000)
    private String content;
}
