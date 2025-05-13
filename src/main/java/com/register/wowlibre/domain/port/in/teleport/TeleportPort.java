package com.register.wowlibre.domain.port.in.teleport;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.*;

import java.util.*;

public interface TeleportPort {
    List<TeleportModel> findByAll(String transactionId);

    void save(TeleportDto teleportModel, String transactionId);
}
