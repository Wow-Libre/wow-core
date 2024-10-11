package com.register.wowlibre.application.services.server;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.mapper.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.server.*;
import com.register.wowlibre.domain.port.out.server.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;

@Repository
public class ServerService implements ServerPort {
    private static final String AVATAR_SERVER_DEFAULT = "https://upload.wikimedia" +
            ".org/wikipedia/commons/thumb/e/eb/WoW_icon.svg/2048px-WoW_icon.svg.png";
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
    public ServerModel findByApiKeyAndStatusIsTrue(String apiKey, String transactionId) {
        return obtainServerPort.findByApiKeyAndStatusIsTrue(apiKey, transactionId).map(ServerMapper::toModel)
                .orElse(null);
    }

    @Override
    public Optional<ServerEntity> findById(Long id, String transactionId) {
        return obtainServerPort.findById(id, transactionId);
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

        ServerModel serverDto = ServerModel.builder()
                .name(serverCreateDto.getName())
                .emulator(serverCreateDto.getEmulator())
                .expansion(serverCreateDto.getExpansion())
                .avatar(AVATAR_SERVER_DEFAULT)
                .ip(serverCreateDto.getIp())
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .password(serverCreateDto.getPassword())
                .creationDate(LocalDateTime.now())
                .status(false)
                .realmlist(serverCreateDto.getRealmlist())
                .webSite(serverCreateDto.getWebSite())
                .build();

        saveServerPort.save(ServerMapper.toEntity(serverDto), transactionId);
    }


    @Override
    public List<ServersDto> findByStatusIsTrue(String transactionId) {
        List<ServerEntity> servers = obtainServerPort.findByStatusIsTrue(transactionId);
        return servers.stream().map(this::mapToModel).toList();
    }

    private ServersDto mapToModel(ServerEntity server) {
        ServersDto serversDto = new ServersDto();
        serversDto.setId(server.getId());
        serversDto.setName(server.getName());
        serversDto.setStatus(server.isStatus());
        serversDto.setEmulator(server.getEmulator());
        serversDto.setExpansion(server.getExpansion());
        serversDto.setCreationDate(server.getCreationDate());
        serversDto.setWebSite(server.getWebSite());
        serversDto.setAvatar(server.getAvatar());
        serversDto.setExpName(Expansion.getById(Integer.parseInt(serversDto.expansion)).getDisplayName());
        return serversDto;
    }

    @Override
    public ServerModel findByNameAndVersionAndStatusIsTrue(String name, String version,
                                                           String transactionId) {
        return obtainServerPort.findByNameAndExpansionAndStatusIsTrue(name, version, transactionId)
                .map(ServerMapper::toModel).orElse(null);
    }


}
