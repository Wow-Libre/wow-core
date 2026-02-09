package com.register.wowlibre.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebUserRowDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String language;
    private Boolean status;
    private Boolean verified;
    private String rolName;
    private int accountCount;
    private List<WebUserAccountGameDto> accounts;
}
