package com.register.wowlibre.application.services.teleport;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.teleport.*;
import com.register.wowlibre.domain.port.out.teleport.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class TeleportService implements TeleportPort {
    private final ObtainTeleport obtainTeleport;
    private final SaveTeleport saveTeleport;

    public TeleportService(ObtainTeleport obtainTeleport, SaveTeleport saveTeleport) {
        this.obtainTeleport = obtainTeleport;
        this.saveTeleport = saveTeleport;
    }

    @Override
    public List<TeleportModel> findByAll(String transactionId) {
        return obtainTeleport.findAllTeleport().stream().map(this::mapModel).toList();
    }

    @Override
    public void save(TeleportDto teleportModel, String transactionId) {
        TeleportEntity teleport = new TeleportEntity();
        teleport.setImgUrl(teleportModel.getImgUrl());
        teleport.setPositionX(teleportModel.getPositionX());
        teleport.setPositionY(teleportModel.getPositionY());
        teleport.setPositionZ(teleportModel.getPositionZ());
        teleport.setMap(teleportModel.getMap());
        teleport.setOrientation(teleportModel.getOrientation());
        teleport.setZona(teleportModel.getZona());
        teleport.setArea(teleportModel.getArea());
        saveTeleport.saveTeleport(teleport);
    }

    private TeleportModel mapModel(TeleportEntity teleport) {
        return TeleportModel.builder()
                .id(teleport.getId())
                .zona(teleport.getZona())
                .imgUrl(teleport.getImgUrl())
                .positionX(teleport.getPositionX())
                .positionY(teleport.getPositionY())
                .positionZ(teleport.getPositionZ())
                .map(teleport.getMap())
                .orientation(teleport.getOrientation())
                .area(teleport.getArea()).build();
    }
}
