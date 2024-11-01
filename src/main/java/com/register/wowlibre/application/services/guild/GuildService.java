package com.register.wowlibre.application.services.guild;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.mapper.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.*;
import com.register.wowlibre.domain.port.in.account_game.*;
import com.register.wowlibre.domain.port.in.benefit_guild.*;
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
    private final ResourcesPort resourcesPort;
    private final BenefitGuildPort benefitGuildPort;
    private final AccountGamePort accountGamePort;

    public GuildService(IntegratorPort integratorPort, ServerPort serverPort, ResourcesPort resourcesPort,
                        BenefitGuildPort benefitGuildPort, AccountGamePort accountGamePort) {
        this.integratorPort = integratorPort;
        this.serverPort = serverPort;
        this.resourcesPort = resourcesPort;
        this.benefitGuildPort = benefitGuildPort;
        this.accountGamePort = accountGamePort;
    }

    @Override
    public GuildsDto findAll(Integer size, Integer page, String search, String serverName, String expansion,
                             String transactionId) {
        ServerModel server;

        if (serverName != null && !serverName.isEmpty() && expansion != null) {
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


        return integratorPort.guilds(server.name, server.id, server.ip, server.jwt, size, page, search, transactionId);
    }

    @Override
    public GuildDto detail(Long serverId, Long guildId, String transactionId) {

        Optional<ServerEntity> server = serverPort.findById(serverId, transactionId);

        if (server.isEmpty() || !server.get().isStatus()) {
            throw new InternalException("", transactionId);
        }

        ServerEntity serverModel = server.get();

        GuildDto guildDto = integratorPort.guild(serverModel.getName(), serverModel.getId(), serverModel.getIp(),
                serverModel.getJwt(), guildId,
                transactionId);

        List<BenefitModel> beneficios = resourcesPort.getBenefitsGuild("es", transactionId);

        List<BenefitGuildModel> beneficiosObtenidos = benefitGuildPort.benefits(serverModel.getId(), guildId,
                transactionId);

        List<BenefitModel> beneficiosFiltrados = beneficios.stream()
                .filter(beneficio -> beneficiosObtenidos.stream()
                        .anyMatch(beneficioObtenido -> beneficioObtenido.benefitId.equals(beneficio.id)))
                .toList();

        guildDto.setBenefits(beneficiosFiltrados);

        return guildDto;
    }

    @Override
    public void attach(Long serverId, Long userId, Long accountId, Long characterId, Long guildId,
                       String transactionId) {

        AccountVerificationDto verificationDto = accountGamePort.verifyAccount(userId, accountId, serverId, transactionId);

        integratorPort.attachGuild(verificationDto.server().getIp(), verificationDto.server().getJwt(),
                verificationDto.accountGame().getAccountId(), guildId, characterId, transactionId);
    }

    @Override
    public void unInviteGuild(Long serverId, Long userId, Long accountId, Long characterId, String transactionId) {

        AccountVerificationDto verificationDto = accountGamePort.verifyAccount(userId, accountId, serverId, transactionId);

        integratorPort.unInviteGuild(verificationDto.server().getIp(), verificationDto.server().getJwt(), userId,
                accountId, characterId, transactionId);
    }

    @Override
    public GuildMemberDetailDto guildMember(Long serverId, Long userId, Long accountId, Long characterId,
                                            String transactionId) {
        AccountVerificationDto verificationDto = accountGamePort.verifyAccount(userId, accountId, serverId, transactionId);

        GuildDetailMemberResponse response = integratorPort.guildMember(verificationDto.server().getIp(),
                verificationDto.server().getJwt(), userId,
                accountId, characterId, transactionId);


        return GuildMemberDetailDto.builder()
                .id(response.getId())
                .name(response.getName())
                .leaderName(response.getLeaderName())
                .emblemColor(response.getEmblemColor())
                .borderStyle(response.getBorderStyle())
                .borderColor(response.getBorderColor())
                .info(response.getInfo())
                .motd(response.getMotd())
                .createDate(response.getCreateDate())
                .bankMoney(response.getBankMoney())
                .members(response.getMembers())
                .publicAccess(response.isPublicAccess())
                .formattedBankMoney(response.formattedBankMoney)
                .members(response.getMembers()).build();
    }
}
