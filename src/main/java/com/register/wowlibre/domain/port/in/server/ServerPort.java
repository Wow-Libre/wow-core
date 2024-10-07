package com.register.wowlibre.domain.port.in.server;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ServerPort {
    List<ServersDto> findByStatusIsTrue(String transactionId);

    ServerModel findByNameAndVersionAndStatusIsTrue(String name, String version, String transactionId);

    ServerModel findByApiKeyAndStatusIsTrue(String apiKey, String transactionId);

    Optional<ServerEntity> findById(Long id, String transactionId);

    void create(ServerCreateDto serverCreateDto, String transactionId);

    void update(String name, String avatar, String ip, String password, String oldPassword, String website,
                String transactionId);
}
