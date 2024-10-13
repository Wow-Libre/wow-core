package com.register.wowlibre.application.services.guild;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.mapper.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.guild.*;
import com.register.wowlibre.domain.port.in.integrator.*;
import com.register.wowlibre.domain.port.in.server.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class GuildService implements GuildPort {

    private final IntegratorPort integratorPort;
    private final ServerPort serverPort;

    public GuildService(IntegratorPort integratorPort, ServerPort serverPort) {
        this.integratorPort = integratorPort;
        this.serverPort = serverPort;
    }

    @Override
    public GuildsDto findAll(Integer size, Integer page, String search, String serverName, String expansion,
                             String transactionId) {
        ServerModel server;

        if (serverName != null && expansion != null) {
            server = serverPort.findByNameAndVersionAndStatusIsTrue(serverName, expansion, transactionId);

            if (server == null) {
                return new GuildsDto(new ArrayList<>(), 0L);
            }

        } else {

            Optional<ServerEntity> servers = serverPort.findByStatusIsTrueServers(transactionId).stream().findFirst();

            if (servers.isEmpty()) {
                return new GuildsDto(new ArrayList<>(), 0L);
            }

            server = ServerMapper.toModel(servers.get());
        }


        return integratorPort.guilds(server.ip, server.jwt, size, page, search, transactionId);
    }

    @Override
    public GuildDto detail(Long serverId, Long guildId, String transactionId) {
        Optional<ServerEntity> server = serverPort.findById(serverId, transactionId);

        if (server.isEmpty() || !server.get().isStatus()) {
            throw new InternalException("", transactionId);
        }
        ServerEntity serverModel = server.get();

        return integratorPort.guild(serverModel.getIp(), serverModel.getJwt(), guildId, transactionId);
    }
}
