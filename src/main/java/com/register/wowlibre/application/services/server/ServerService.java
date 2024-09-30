package com.register.wowlibre.application.services.server;

import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.mapper.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.server.*;
import com.register.wowlibre.domain.port.out.server.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class ServerService implements ServerPort {
    private final ObtainServerPort obtainServerPort;
    private final SaveServerPort saveServerPort;

    public ServerService(ObtainServerPort obtainServerPort, SaveServerPort saveServerPort) {
        this.obtainServerPort = obtainServerPort;
        this.saveServerPort = saveServerPort;
    }

    @Override
    public List<ServerModel> findByStatusIsTrue(String transactionId) {
        return obtainServerPort.findByStatusIsTrue(transactionId).stream().map(ServerMapper::toModel).toList();
    }

    @Override
    public ServerModel findByNameAndVersionAndStatusIsTrue(String name, String version,
                                                           String transactionId) {
        return obtainServerPort.findByNameAndVersionAndStatusIsTrue(name, version, transactionId)
                .map(ServerMapper::toModel).orElse(null);
    }

    @Override
    public void create(ServerModel serverDto, String transactionId) {

        if (obtainServerPort.findByNameAndVersion(serverDto.name, serverDto.version, transactionId).isPresent()) {
            throw new InternalException("It is not possible to create or configure a server with because one already " +
                    "exists with the same name and with the same version characteristics.", transactionId);
        }

        saveServerPort.save(ServerMapper.toEntity(serverDto), transactionId);

    }
}
