package com.register.wowlibre.domain.port.in.server;

import com.register.wowlibre.domain.model.*;

import java.util.*;

public interface ServerPort {
    List<ServerModel> findByStatusIsTrue(String transactionId);

    ServerModel findByNameAndVersionAndStatusIsTrue(String name, String emulator, String transactionId);

    void create(ServerModel serverDto, String transactionId);
}
