package com.register.wowlibre.domain.port.in.machine;

import com.register.wowlibre.domain.dto.*;

import java.util.*;

public interface MachinePort {
    MachineDto evaluate(Long userId, Long accountId, Long characterId, Long serverId, Locale locale,
                        String transactionId);

    MachineDetailDto coins(Long userId, Long serverId, String transactionId);
}
