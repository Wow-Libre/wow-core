package com.register.wowlibre.domain.dto;

import com.register.wowlibre.domain.model.*;
import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
public class GuildsDto {
    private List<GuildModel> guilds;
    private Long size;
}
