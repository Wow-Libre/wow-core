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
public class WebUsersPageDto {
    private List<WebUserRowDto> content;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;
}
