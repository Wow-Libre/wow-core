package com.register.wowlibre.domain.port.out.teleport;

import com.register.wowlibre.infrastructure.entities.*;

public interface SaveTeleport {
    void saveTeleport(TeleportEntity teleportEntity);
}
