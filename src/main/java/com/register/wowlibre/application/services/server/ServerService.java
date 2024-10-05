package com.register.wowlibre.application.services.server;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.mapper.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.server.*;
import com.register.wowlibre.domain.port.out.server.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class ServerService implements ServerPort {
    private final String AVATAR_SERVER_DEFAULT = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/eb/WoW_icon" +
            ".svg/2048px-WoW_icon.svg.png";
    private final ObtainServerPort obtainServerPort;
    private final SaveServerPort saveServerPort;

    private final RandomString randomString;

    public ServerService(ObtainServerPort obtainServerPort, SaveServerPort saveServerPort,
                         @Qualifier("random-string") RandomString randomString) {
        this.obtainServerPort = obtainServerPort;
        this.saveServerPort = saveServerPort;
        this.randomString = randomString;
    }

    @Override
    public List<ServerModel> findByStatusIsTrue(String transactionId) {
        List<ServerEntity> servers = obtainServerPort.findByStatusIsTrue(transactionId);

        return servers.stream().map(ServerMapper::toModel).toList();
    }

    @Override
    public ServerModel findByNameAndVersionAndStatusIsTrue(String name, String version,
                                                           String transactionId) {
        return obtainServerPort.findByNameAndExpansionAndStatusIsTrue(name, version, transactionId)
                .map(ServerMapper::toModel).orElse(null);
    }

    @Override
    public ServerModel findByApiKeyAndStatusIsTrue(String apiKey, String transactionId) {
        return obtainServerPort.findByApiKeyAndStatusIsTrue(apiKey, transactionId).map(ServerMapper::toModel)
                .orElse(null);
    }

    @Override
    public void create(ServerCreateDto serverCreateDto, String transactionId) {


        if (obtainServerPort.findByNameAndExpansion(serverCreateDto.getName(), serverCreateDto.getExpansion(),
                transactionId).isPresent()) {
            throw new InternalException("It is not possible to create or configure a server with because one already " +
                    "exists with the same name and with the same version characteristics.", transactionId);
        }

        final String apiKey = randomString.nextString();
        final String apiSecret = randomString.nextString();

        ServerModel serverDto = ServerMapper.create(serverCreateDto, apiKey, apiSecret, AVATAR_SERVER_DEFAULT, false);

        saveServerPort.save(ServerMapper.toEntity(serverDto), transactionId);
    }

    @Override
    public void update(String name, String avatar, String ip, String password, String oldPassword, String website,
                       String transactionId) {

    }
}
