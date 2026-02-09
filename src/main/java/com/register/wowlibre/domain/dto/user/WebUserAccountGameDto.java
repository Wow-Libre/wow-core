package com.register.wowlibre.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebUserAccountGameDto {
    private Long id;
    private String username;
    private String gameEmail;
    private Long accountId;
    private boolean status;
    private Long realmId;
    private String realmName;
}
