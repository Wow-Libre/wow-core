package com.register.wowlibre.domain.dto;

import com.register.wowlibre.domain.model.*;
import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
public class AccountsDto {
    private List<AccountGameModel> accounts;
    private Long size;
}
