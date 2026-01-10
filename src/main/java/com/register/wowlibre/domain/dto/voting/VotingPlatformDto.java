package com.register.wowlibre.domain.dto.voting;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class VotingPlatformDto {
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String imgUrl;
    @NotNull
    @NotEmpty
    private String postbackUrl;
    private String allowedHost;
}
