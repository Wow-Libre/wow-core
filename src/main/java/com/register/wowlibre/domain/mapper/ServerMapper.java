package com.register.wowlibre.domain.mapper;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.infrastructure.entities.*;

import java.time.*;

public class ServerMapper {


    public static ServerModel toModel(ServerEntity server) {
        if (server == null) {
            return null;
        }
        return ServerModel.builder().avatar(server.getAvatar()).name(server.getName())
                .creationDate(server.getCreationDate())
                .ip(server.getIp())
                .emulator(server.getEmulator())
                .status(server.isStatus())
                .version(server.getVersion())
                .apiSecret(server.getApiSecret())
                .webSite(server.getWebSite())
                .avatar(server.getAvatar())
                .apiKey(server.getApiKey())
                .password(server.getPassword())
                .build();
    }

    public static ServerEntity toEntity(ServerModel server) {
        if (server == null) {
            return null;
        }

        ServerEntity serverEntity = new ServerEntity();
        serverEntity.setId(server.id);
        serverEntity.setName(server.name);
        serverEntity.setEmulator(server.emulator);
        serverEntity.setVersion(server.version);
        serverEntity.setAvatar(server.avatar);
        serverEntity.setIp(server.ip);
        serverEntity.setApiKey(server.apiKey);
        serverEntity.setStatus(server.status);
        serverEntity.setPassword(server.password);
        serverEntity.setWebSite(server.webSite);
        serverEntity.setCreationDate(server.creationDate);
        return serverEntity;
    }

    public static ServerModel create(ServerDto server, String apiKey, String apiSecret, String avatar, boolean status) {

        if (server == null) {
            return null;
        }

        return ServerModel.builder()
                .name(server.getName())
                .emulator(server.getEmulator())
                .version(server.getVersion())
                .avatar(avatar)
                .ip(server.getIp())
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .password(server.getPassword())
                .creationDate(LocalDateTime.now())
                .status(status)
                .webSite(server.getWebSite())
                .build();
    }


}
