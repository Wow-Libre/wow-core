package com.register.wowlibre.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.dto.guilds.*;
import com.register.wowlibre.domain.port.in.guild.GuildPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.infrastructure.controller.GuildController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GuildControllerTest {

    @Mock
    private GuildPort guildPort;

    @InjectMocks
    private GuildController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnGuilds() {
        String transactionId = "tx-guild-001";
        Integer size = 10;
        Integer page = 0;
        String search = "test";
        String realm = "Test Realm";
        Integer expansionId = 2;
        GuildsDto guildsDto = new GuildsDto(new ArrayList<>(), 0L);

        when(guildPort.findAll(size, page, search, realm, expansionId, transactionId))
                .thenReturn(guildsDto);

        ResponseEntity<GenericResponse<GuildsDto>> response = controller.guilds(
                transactionId, size, page, search, realm, expansionId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(guildPort).findAll(size, page, search, realm, expansionId, transactionId);
    }

    @Test
    void shouldReturnNoContentWhenGuildsIsNull() {
        String transactionId = "tx-guild-002";
        Integer size = 10;
        Integer page = 0;
        String search = "test";
        String realm = "Test Realm";
        Integer expansionId = 2;

        when(guildPort.findAll(size, page, search, realm, expansionId, transactionId))
                .thenReturn(null);

        ResponseEntity<GenericResponse<GuildsDto>> response = controller.guilds(
                transactionId, size, page, search, realm, expansionId
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(guildPort).findAll(size, page, search, realm, expansionId, transactionId);
    }

    @Test
    void shouldReturnGuild() {
        String transactionId = "tx-guild-003";
        long guildId = 1L;
        long realmId = 1L;
        Locale locale = Locale.ENGLISH;
        GuildDto guildDto = new GuildDto(1L, "Test Guild", "Leader", 1L, 1L, 1L, 1L,
                "Info", "MOTD", new java.util.Date(), 1000L, 10L, true, null,
                "1,000", "Test Realm", 1L, false, "discord#1234", new ArrayList<>());

        when(guildPort.detail(realmId, guildId, locale, transactionId)).thenReturn(guildDto);

        ResponseEntity<GenericResponse<GuildDto>> response = controller.guild(
                transactionId, guildId, realmId, locale
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(guildPort).detail(realmId, guildId, locale, transactionId);
    }

    @Test
    void shouldAttachGuild() {
        String transactionId = "tx-guild-004";
        long userId = 1L;
        GuildAttachDto request = new GuildAttachDto();
        request.setServerId(1L);
        request.setAccountId(101L);
        request.setCharacterId(201L);
        request.setGuildId(1L);

        ResponseEntity<GenericResponse<Void>> response = controller.attach(
                transactionId, userId, request
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(guildPort).attach(request.getServerId(), userId, request.getAccountId(),
                request.getCharacterId(), request.getGuildId(), transactionId);
    }

    @Test
    void shouldReturnMemberGuild() {
        String transactionId = "tx-guild-005";
        long serverId = 1L;
        long userId = 1L;
        long accountId = 101L;
        long characterId = 201L;
        GuildMemberDetailDto memberDto = GuildMemberDetailDto.builder().build();

        when(guildPort.guildMember(serverId, userId, accountId, characterId, transactionId))
                .thenReturn(memberDto);

        ResponseEntity<GenericResponse<GuildMemberDetailDto>> response = controller.memberGuild(
                transactionId, serverId, userId, accountId, characterId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(guildPort).guildMember(serverId, userId, accountId, characterId, transactionId);
    }

    @Test
    void shouldUnInviteGuild() {
        String transactionId = "tx-guild-006";
        long userId = 1L;
        UnInviteGuildDto request = new UnInviteGuildDto();
        request.setServerId(1L);
        request.setAccountId(101L);
        request.setCharacterId(201L);

        ResponseEntity<GenericResponse<Void>> response = controller.unInviteGuild(
                transactionId, userId, request
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(guildPort).unInviteGuild(request.getServerId(), userId, request.getAccountId(),
                request.getCharacterId(), transactionId);
    }

    @Test
    void shouldUpdateGuild() {
        String transactionId = "tx-guild-007";
        long userId = 1L;
        UpdateGuildDto request = new UpdateGuildDto();
        request.setServerId(1L);
        request.setAccountId(101L);
        request.setCharacterId(201L);
        request.setDiscord("discord#1234");
        request.setMultiFaction(true);
        request.setPublic(true);

        ResponseEntity<GenericResponse<Void>> response = controller.update(
                transactionId, userId, request
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(guildPort).update(request.getServerId(), userId, request.getAccountId(),
                request.getCharacterId(), request.getDiscord(), request.isMultiFaction(),
                request.isPublic(), transactionId);
    }

    @Test
    void shouldClaimBenefits() {
        String transactionId = "tx-guild-008";
        long userId = 1L;
        Locale locale = Locale.ENGLISH;
        ClaimBenefitsGuildDto request = new ClaimBenefitsGuildDto();
        request.setServerId(1L);
        request.setAccountId(101L);
        request.setCharacterId(201L);

        ResponseEntity<GenericResponse<Void>> response = controller.benefits(
                transactionId, userId, locale, request
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(guildPort).claimBenefits(request.getServerId(), userId, request.getAccountId(),
                request.getCharacterId(), locale.getLanguage(), transactionId);
    }
}

