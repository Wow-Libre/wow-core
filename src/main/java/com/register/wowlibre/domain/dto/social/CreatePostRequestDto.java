package com.register.wowlibre.domain.dto.social;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostRequestDto {

    @Size(max = 8000)
    private String content;

    @NotNull
    @Size(max = 10)
    @Builder.Default
    private List<String> mediaUrls = new ArrayList<>();
}
