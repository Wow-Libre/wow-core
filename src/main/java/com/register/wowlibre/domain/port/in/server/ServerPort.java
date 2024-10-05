package com.register.wowlibre.domain.port.in.server;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.*;

import java.util.*;

public interface ServerPort {
    List<ServerModel> findByStatusIsTrue(String transactionId);

    ServerModel findByNameAndVersionAndStatusIsTrue(String name, String version, String transactionId);

    ServerModel findByApiKeyAndStatusIsTrue(String apiKey, String transactionId);

    void create(ServerCreateDto serverCreateDto, String transactionId);

    void update(String name, String avatar, String ip, String password, String oldPassword, String website,
                String transactionId);
}
