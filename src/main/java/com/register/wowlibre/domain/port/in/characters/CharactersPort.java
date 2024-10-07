package com.register.wowlibre.domain.port.in.characters;

import com.register.wowlibre.domain.dto.*;

public interface CharactersPort {
    CharactersDto characters(Long userId, Long accountId, Long serverId, String transactionId);
}
