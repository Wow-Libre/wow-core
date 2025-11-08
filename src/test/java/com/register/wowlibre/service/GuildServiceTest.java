package com.register.wowlibre.service;

import com.register.wowlibre.application.services.guild.GuildService;
import com.register.wowlibre.domain.dto.account_game.AccountVerificationDto;
import com.register.wowlibre.domain.dto.client.GuildDetailMemberResponse;
import com.register.wowlibre.domain.dto.guilds.GuildDto;
import com.register.wowlibre.domain.dto.guilds.GuildMemberDetailDto;
import com.register.wowlibre.domain.dto.guilds.GuildsDto;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.mapper.RealmMapper;
import com.register.wowlibre.domain.model.RealmModel;
import com.register.wowlibre.domain.model.resources.BenefitModel;
import com.register.wowlibre.domain.port.in.ResourcesPort;
import com.register.wowlibre.domain.port.in.account_validation.AccountValidationPort;
import com.register.wowlibre.domain.port.in.benefit_guild.BenefitGuildPort;
import com.register.wowlibre.domain.port.in.character_benefit_guild.CharacterBenefitGuildPort;
import com.register.wowlibre.domain.port.in.integrator.IntegratorPort;
import com.register.wowlibre.domain.port.in.realm.RealmPort;
import com.register.wowlibre.infrastructure.entities.AccountGameEntity;
import com.register.wowlibre.infrastructure.entities.BenefitGuildEntity;
import com.register.wowlibre.infrastructure.entities.RealmEntity;
import com.register.wowlibre.model.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GuildServiceTest extends BaseTest {

    @Mock
    private IntegratorPort integratorPort;
    @Mock
    private RealmPort realmPort;
    @Mock
    private ResourcesPort resourcesPort;
    @Mock
    private BenefitGuildPort benefitGuildPort;
    @Mock
    private CharacterBenefitGuildPort characterBenefitGuildPort;
    @Mock
    private AccountValidationPort accountValidationPort;

    private GuildService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new GuildService(integratorPort, realmPort, resourcesPort, benefitGuildPort,
                characterBenefitGuildPort, accountValidationPort);
    }

    @Test
    void findAll_shouldReturnGuildsWhenRealmNameAndExpansionProvided() {
        Integer size = 10;
        Integer page = 0;
        String search = "test";
        String realmName = "Test Realm";
        Integer expansionId = 2;
        String transactionId = "tx-guild-001";
        RealmModel realmModel = createRealmModel(1L, realmName);
        GuildsDto expectedGuilds = new GuildsDto(new ArrayList<>(), 0L);

        when(realmPort.findByNameAndVersionAndStatusIsTrue(realmName, expansionId, transactionId))
                .thenReturn(realmModel);
        when(integratorPort.guilds(realmModel.name, realmModel.id, realmModel.ip, realmModel.jwt,
                size, page, search, transactionId)).thenReturn(expectedGuilds);

        GuildsDto result = service.findAll(size, page, search, realmName, expansionId, transactionId);

        assertNotNull(result);
        verify(realmPort).findByNameAndVersionAndStatusIsTrue(realmName, expansionId, transactionId);
        verify(integratorPort).guilds(realmModel.name, realmModel.id, realmModel.ip, realmModel.jwt,
                size, page, search, transactionId);
    }

    @Test
    void findAll_shouldReturnEmptyWhenRealmNotFound() {
        Integer size = 10;
        Integer page = 0;
        String search = "test";
        String realmName = "NonExistent Realm";
        Integer expansionId = 2;
        String transactionId = "tx-guild-002";

        when(realmPort.findByNameAndVersionAndStatusIsTrue(realmName, expansionId, transactionId))
                .thenReturn(null);

        GuildsDto result = service.findAll(size, page, search, realmName, expansionId, transactionId);

        assertNotNull(result);
        assertTrue(result.getGuilds().isEmpty());
        assertEquals(0L, result.getSize());
        verify(realmPort).findByNameAndVersionAndStatusIsTrue(realmName, expansionId, transactionId);
        verifyNoInteractions(integratorPort);
    }

    @Test
    void findAll_shouldReturnGuildsWhenNoRealmNameProvided() {
        Integer size = 10;
        Integer page = 0;
        String search = "test";
        String realmName = null;
        Integer expansionId = null;
        String transactionId = "tx-guild-003";
        RealmEntity realmEntity = createRealmEntity(1L, "Test Realm");
        RealmModel realmModel = RealmMapper.toModel(realmEntity);
        GuildsDto expectedGuilds = new GuildsDto(new ArrayList<>(), 0L);

        when(realmPort.findByStatusIsTrueServers(transactionId)).thenReturn(List.of(realmEntity));
        when(integratorPort.guilds(realmModel.name, realmModel.id, realmModel.ip, realmModel.jwt,
                size, page, search, transactionId)).thenReturn(expectedGuilds);

        GuildsDto result = service.findAll(size, page, search, realmName, expansionId, transactionId);

        assertNotNull(result);
        verify(realmPort).findByStatusIsTrueServers(transactionId);
        verify(integratorPort).guilds(realmModel.name, realmModel.id, realmModel.ip, realmModel.jwt,
                size, page, search, transactionId);
    }

    @Test
    void findAll_shouldReturnEmptyWhenNoActiveRealms() {
        Integer size = 10;
        Integer page = 0;
        String search = "test";
        String realmName = null;
        Integer expansionId = null;
        String transactionId = "tx-guild-004";

        when(realmPort.findByStatusIsTrueServers(transactionId)).thenReturn(List.of());

        GuildsDto result = service.findAll(size, page, search, realmName, expansionId, transactionId);

        assertNotNull(result);
        assertTrue(result.getGuilds().isEmpty());
        assertEquals(0L, result.getSize());
        verify(realmPort).findByStatusIsTrueServers(transactionId);
        verifyNoInteractions(integratorPort);
    }

    @Test
    void detail_shouldReturnGuildDtoWithBenefits() {
        Long realmId = 1L;
        Long guildId = 1L;
        Locale locale = Locale.ENGLISH;
        String transactionId = "tx-guild-005";
        RealmEntity realm = createRealmEntity(realmId, "Test Realm");
        GuildDto guildDto = createGuildDto(guildId);
        BenefitModel benefit1 = createBenefitModel(1L, "ITEM001");
        BenefitModel benefit2 = createBenefitModel(2L, "ITEM002");
        BenefitGuildEntity benefitGuild1 = createBenefitGuildEntity(1L, realmId, guildId, 1L);
        BenefitGuildEntity benefitGuild2 = createBenefitGuildEntity(2L, realmId, guildId, 2L);

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.of(realm));
        when(integratorPort.guild(realm.getName(), realm.getId(), realm.getHost(), realm.getJwt(),
                guildId, transactionId)).thenReturn(guildDto);
        when(resourcesPort.getBenefitsGuild(locale.getLanguage(), transactionId))
                .thenReturn(List.of(benefit1, benefit2));
        when(benefitGuildPort.benefits(realmId, guildId, transactionId))
                .thenReturn(List.of(benefitGuild1, benefitGuild2));

        GuildDto result = service.detail(realmId, guildId, locale, transactionId);

        assertNotNull(result);
        assertEquals(2, result.getBenefits().size());
        verify(realmPort).findById(realmId, transactionId);
        verify(integratorPort).guild(realm.getName(), realm.getId(), realm.getHost(), realm.getJwt(),
                guildId, transactionId);
        verify(resourcesPort).getBenefitsGuild(locale.getLanguage(), transactionId);
        verify(benefitGuildPort).benefits(realmId, guildId, transactionId);
    }

    @Test
    void detail_shouldThrowExceptionWhenRealmNotFound() {
        Long realmId = 999L;
        Long guildId = 1L;
        Locale locale = Locale.ENGLISH;
        String transactionId = "tx-guild-006";

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.empty());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.detail(realmId, guildId, locale, transactionId)
        );

        assertEquals("The Realm is not available", exception.getMessage());
        verify(realmPort).findById(realmId, transactionId);
        verifyNoInteractions(integratorPort, resourcesPort, benefitGuildPort);
    }

    @Test
    void detail_shouldThrowExceptionWhenRealmNotActive() {
        Long realmId = 1L;
        Long guildId = 1L;
        Locale locale = Locale.ENGLISH;
        String transactionId = "tx-guild-007";
        RealmEntity realm = createRealmEntity(realmId, "Test Realm");
        realm.setStatus(false);

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.of(realm));

        InternalException exception = assertThrows(InternalException.class, () ->
                service.detail(realmId, guildId, locale, transactionId)
        );

        assertEquals("The Realm is not available", exception.getMessage());
        verify(realmPort).findById(realmId, transactionId);
        verifyNoInteractions(integratorPort, resourcesPort, benefitGuildPort);
    }

    @Test
    void attach_shouldAttachCharacterToGuild() {
        Long serverId = 1L;
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        Long guildId = 1L;
        String transactionId = "tx-guild-008";
        RealmEntity realm = createRealmEntity(serverId, "Test Realm");
        AccountGameEntity accountGame = createAccountGameEntity(accountId);
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, accountGame);

        when(accountValidationPort.verifyAccount(userId, accountId, serverId, transactionId))
                .thenReturn(verificationDto);

        service.attach(serverId, userId, accountId, characterId, guildId, transactionId);

        verify(accountValidationPort).verifyAccount(userId, accountId, serverId, transactionId);
        verify(integratorPort).attachGuild(realm.getHost(), realm.getJwt(), accountGame.getAccountId(),
                guildId, characterId, transactionId);
    }

    @Test
    void unInviteGuild_shouldUninviteCharacterFromGuild() {
        Long serverId = 1L;
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        String transactionId = "tx-guild-009";
        RealmEntity realm = createRealmEntity(serverId, "Test Realm");
        AccountGameEntity accountGame = createAccountGameEntity(accountId);
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, accountGame);

        when(accountValidationPort.verifyAccount(userId, accountId, serverId, transactionId))
                .thenReturn(verificationDto);

        service.unInviteGuild(serverId, userId, accountId, characterId, transactionId);

        verify(accountValidationPort).verifyAccount(userId, accountId, serverId, transactionId);
        verify(integratorPort).unInviteGuild(realm.getHost(), realm.getJwt(), userId,
                accountId, characterId, transactionId);
    }

    @Test
    void guildMember_shouldReturnGuildMemberDetailDto() {
        Long serverId = 1L;
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        String transactionId = "tx-guild-010";
        RealmEntity realm = createRealmEntity(serverId, "Test Realm");
        AccountGameEntity accountGame = createAccountGameEntity(accountId);
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, accountGame);
        GuildDetailMemberResponse response = createGuildDetailMemberResponse(1L);
        BenefitGuildEntity benefit1 = createBenefitGuildEntity(1L, serverId, 1L, 1L);

        when(accountValidationPort.verifyAccount(userId, accountId, serverId, transactionId))
                .thenReturn(verificationDto);
        when(integratorPort.guildMember(realm.getHost(), realm.getJwt(), userId, accountId, characterId, transactionId))
                .thenReturn(response);
        when(benefitGuildPort.findRemainingBenefitsForGuildAndServerIdAndCharacter(serverId, response.getId(),
                characterId, accountId, transactionId)).thenReturn(List.of(benefit1));

        GuildMemberDetailDto result = service.guildMember(serverId, userId, accountId, characterId, transactionId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Guild", result.getName());
        assertEquals(1, result.getAvailableBenefits());
        verify(accountValidationPort).verifyAccount(userId, accountId, serverId, transactionId);
        verify(integratorPort).guildMember(realm.getHost(), realm.getJwt(), userId, accountId, characterId, transactionId);
        verify(benefitGuildPort).findRemainingBenefitsForGuildAndServerIdAndCharacter(serverId, response.getId(),
                characterId, accountId, transactionId);
    }

    @Test
    void update_shouldUpdateGuild() {
        Long serverId = 1L;
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        String discord = "discord#1234";
        boolean multiFaction = true;
        boolean isPublic = true;
        String transactionId = "tx-guild-011";
        RealmEntity realm = createRealmEntity(serverId, "Test Realm");
        AccountGameEntity accountGame = createAccountGameEntity(accountId);
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, accountGame);

        when(accountValidationPort.verifyAccount(userId, accountId, serverId, transactionId))
                .thenReturn(verificationDto);

        service.update(serverId, userId, accountId, characterId, discord, multiFaction, isPublic, transactionId);

        verify(accountValidationPort).verifyAccount(userId, accountId, serverId, transactionId);
        verify(integratorPort).updateGuild(realm.getHost(), realm.getJwt(), characterId, accountId,
                isPublic, multiFaction, discord, transactionId);
    }

    @Test
    void claimBenefits_shouldClaimBenefitsSuccessfully() {
        Long serverId = 1L;
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        String language = "es";
        String transactionId = "tx-guild-012";
        RealmEntity realm = createRealmEntity(serverId, "Test Realm");
        AccountGameEntity accountGame = createAccountGameEntity(accountId);
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, accountGame);
        GuildDetailMemberResponse guildResponse = createGuildDetailMemberResponse(1L);
        BenefitModel benefit1 = createBenefitModel(1L, "ITEM001");
        BenefitModel benefit2 = createBenefitModel(2L, "ITEM002");
        BenefitGuildEntity benefitGuild1 = createBenefitGuildEntity(1L, serverId, 1L, 1L);
        BenefitGuildEntity benefitGuild2 = createBenefitGuildEntity(2L, serverId, 1L, 2L);

        when(accountValidationPort.verifyAccount(userId, accountId, serverId, transactionId))
                .thenReturn(verificationDto);
        when(integratorPort.guildMember(realm.getHost(), realm.getJwt(), userId, accountId, characterId, transactionId))
                .thenReturn(guildResponse);
        when(resourcesPort.getBenefitsGuild(language, transactionId))
                .thenReturn(List.of(benefit1, benefit2));
        when(benefitGuildPort.findRemainingBenefitsForGuildAndServerIdAndCharacter(serverId, guildResponse.getId(),
                characterId, accountId, transactionId)).thenReturn(List.of(benefitGuild1, benefitGuild2));

        service.claimBenefits(serverId, userId, accountId, characterId, language, transactionId);

        verify(accountValidationPort).verifyAccount(userId, accountId, serverId, transactionId);
        verify(integratorPort).guildMember(realm.getHost(), realm.getJwt(), userId, accountId, characterId, transactionId);
        verify(resourcesPort).getBenefitsGuild(language, transactionId);
        verify(benefitGuildPort).findRemainingBenefitsForGuildAndServerIdAndCharacter(serverId, guildResponse.getId(),
                characterId, accountId, transactionId);
        verify(integratorPort).sendGuildBenefit(eq(realm.getHost()), eq(realm.getJwt()), eq(userId), eq(accountId),
                eq(characterId), any(), eq(transactionId));
        verify(characterBenefitGuildPort, times(2)).save(eq(characterId), eq(accountId), any(), eq(true), eq(transactionId));
    }

    @Test
    void claimBenefits_shouldThrowExceptionWhenGuildMemberIsNull() {
        Long serverId = 1L;
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        String language = "es";
        String transactionId = "tx-guild-013";
        RealmEntity realm = createRealmEntity(serverId, "Test Realm");
        AccountGameEntity accountGame = createAccountGameEntity(accountId);
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, accountGame);

        when(accountValidationPort.verifyAccount(userId, accountId, serverId, transactionId))
                .thenReturn(verificationDto);
        when(integratorPort.guildMember(realm.getHost(), realm.getJwt(), userId, accountId, characterId, transactionId))
                .thenReturn(null);

        InternalException exception = assertThrows(InternalException.class, () ->
                service.claimBenefits(serverId, userId, accountId, characterId, language, transactionId)
        );

        assertEquals("You do not belong to a guild or an unexpected error has occurred", exception.getMessage());
        verify(accountValidationPort).verifyAccount(userId, accountId, serverId, transactionId);
        verify(integratorPort).guildMember(realm.getHost(), realm.getJwt(), userId, accountId, characterId, transactionId);
        verifyNoInteractions(resourcesPort, benefitGuildPort, characterBenefitGuildPort);
    }

    @Test
    void claimBenefits_shouldThrowExceptionWhenNoBenefitsAvailable() {
        Long serverId = 1L;
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        String language = "es";
        String transactionId = "tx-guild-014";
        RealmEntity realm = createRealmEntity(serverId, "Test Realm");
        AccountGameEntity accountGame = createAccountGameEntity(accountId);
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, accountGame);
        GuildDetailMemberResponse guildResponse = createGuildDetailMemberResponse(1L);

        when(accountValidationPort.verifyAccount(userId, accountId, serverId, transactionId))
                .thenReturn(verificationDto);
        when(integratorPort.guildMember(realm.getHost(), realm.getJwt(), userId, accountId, characterId, transactionId))
                .thenReturn(guildResponse);
        when(resourcesPort.getBenefitsGuild(language, transactionId)).thenReturn(List.of());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.claimBenefits(serverId, userId, accountId, characterId, language, transactionId)
        );

        assertEquals("There are no benefits available to purchase", exception.getMessage());
        verify(accountValidationPort).verifyAccount(userId, accountId, serverId, transactionId);
        verify(integratorPort).guildMember(realm.getHost(), realm.getJwt(), userId, accountId, characterId, transactionId);
        verify(resourcesPort).getBenefitsGuild(language, transactionId);
        verifyNoInteractions(benefitGuildPort, characterBenefitGuildPort);
    }

    @Test
    void claimBenefits_shouldThrowExceptionWhenNoRemainingBenefits() {
        Long serverId = 1L;
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        String language = "es";
        String transactionId = "tx-guild-015";
        RealmEntity realm = createRealmEntity(serverId, "Test Realm");
        AccountGameEntity accountGame = createAccountGameEntity(accountId);
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, accountGame);
        GuildDetailMemberResponse guildResponse = createGuildDetailMemberResponse(1L);
        BenefitModel benefit1 = createBenefitModel(1L, "ITEM001");

        when(accountValidationPort.verifyAccount(userId, accountId, serverId, transactionId))
                .thenReturn(verificationDto);
        when(integratorPort.guildMember(realm.getHost(), realm.getJwt(), userId, accountId, characterId, transactionId))
                .thenReturn(guildResponse);
        when(resourcesPort.getBenefitsGuild(language, transactionId)).thenReturn(List.of(benefit1));
        when(benefitGuildPort.findRemainingBenefitsForGuildAndServerIdAndCharacter(serverId, guildResponse.getId(),
                characterId, accountId, transactionId)).thenReturn(List.of());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.claimBenefits(serverId, userId, accountId, characterId, language, transactionId)
        );

        assertEquals("There are no benefits available to purchase", exception.getMessage());
        verify(accountValidationPort).verifyAccount(userId, accountId, serverId, transactionId);
        verify(integratorPort).guildMember(realm.getHost(), realm.getJwt(), userId, accountId, characterId, transactionId);
        verify(resourcesPort).getBenefitsGuild(language, transactionId);
        verify(benefitGuildPort).findRemainingBenefitsForGuildAndServerIdAndCharacter(serverId, guildResponse.getId(),
                characterId, accountId, transactionId);
        verifyNoInteractions(characterBenefitGuildPort);
    }

    @Test
    void claimBenefits_shouldThrowExceptionWhenNoFilteredBenefits() {
        Long serverId = 1L;
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        String language = "es";
        String transactionId = "tx-guild-016";
        RealmEntity realm = createRealmEntity(serverId, "Test Realm");
        AccountGameEntity accountGame = createAccountGameEntity(accountId);
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, accountGame);
        GuildDetailMemberResponse guildResponse = createGuildDetailMemberResponse(1L);
        BenefitModel benefit1 = createBenefitModel(1L, "ITEM001");
        BenefitGuildEntity benefitGuild1 = createBenefitGuildEntity(1L, serverId, 1L, 999L); // Different benefit ID

        when(accountValidationPort.verifyAccount(userId, accountId, serverId, transactionId))
                .thenReturn(verificationDto);
        when(integratorPort.guildMember(realm.getHost(), realm.getJwt(), userId, accountId, characterId, transactionId))
                .thenReturn(guildResponse);
        when(resourcesPort.getBenefitsGuild(language, transactionId)).thenReturn(List.of(benefit1));
        when(benefitGuildPort.findRemainingBenefitsForGuildAndServerIdAndCharacter(serverId, guildResponse.getId(),
                characterId, accountId, transactionId)).thenReturn(List.of(benefitGuild1));

        InternalException exception = assertThrows(InternalException.class, () ->
                service.claimBenefits(serverId, userId, accountId, characterId, language, transactionId)
        );

        assertEquals("There are no benefits available to purchase", exception.getMessage());
        verify(accountValidationPort).verifyAccount(userId, accountId, serverId, transactionId);
        verify(integratorPort).guildMember(realm.getHost(), realm.getJwt(), userId, accountId, characterId, transactionId);
        verify(resourcesPort).getBenefitsGuild(language, transactionId);
        verify(benefitGuildPort).findRemainingBenefitsForGuildAndServerIdAndCharacter(serverId, guildResponse.getId(),
                characterId, accountId, transactionId);
        verifyNoInteractions(characterBenefitGuildPort);
    }

    private RealmModel createRealmModel(Long id, String name) {
        return RealmModel.builder()
                .id(id)
                .name(name)
                .status(true)
                .ip("http://test.com")
                .jwt("jwt-token")
                .build();
    }

    private RealmEntity createRealmEntity(Long id, String name) {
        RealmEntity realm = new RealmEntity();
        realm.setId(id);
        realm.setName(name);
        realm.setStatus(true);
        realm.setHost("http://test.com");
        realm.setJwt("jwt-token");
        realm.setExpansionId(2);
        return realm;
    }

    private AccountGameEntity createAccountGameEntity(Long accountId) {
        AccountGameEntity accountGame = new AccountGameEntity();
        accountGame.setId(accountId);
        accountGame.setAccountId(accountId);
        accountGame.setStatus(true);
        return accountGame;
    }

    private GuildDto createGuildDto(Long id) {
        return new GuildDto(id, "Test Guild", "Leader", 1L, 1L, 1L, 1L,
                "Info", "MOTD", new Date(), 1000L, 10L, true, null,
                "1,000", "Test Realm", 1L, false, "discord#1234", new ArrayList<>());
    }

    private BenefitModel createBenefitModel(Long id, String itemId) {
        return new BenefitModel(id, "Title", "SubTitle", "Description", "logo.jpg",
                itemId, 1, true, "link");
    }

    private BenefitGuildEntity createBenefitGuildEntity(Long id, Long realmId, Long guildId, Long benefitId) {
        BenefitGuildEntity entity = new BenefitGuildEntity();
        entity.setId(id);
        entity.setRealmId(createRealmEntity(realmId, "Test Realm"));
        entity.setGuildId(guildId);
        entity.setBenefitId(benefitId);
        entity.setStatus(true);
        return entity;
    }

    private GuildDetailMemberResponse createGuildDetailMemberResponse(Long id) {
        GuildDetailMemberResponse response = new GuildDetailMemberResponse();
        response.setId(id);
        response.setName("Test Guild");
        response.setLeaderName("Leader");
        response.setEmblemColor(1L);
        response.setBorderStyle(1L);
        response.setBorderColor(1L);
        response.setInfo("Info");
        response.setMotd("MOTD");
        response.setIsLeader(true);
        response.setMultiFaction(true);
        response.setDiscord("discord#1234");
        response.setCreateDate(new Date());
        response.setBankMoney(1000L);
        response.setMembers(10L);
        response.setPublicAccess(true);
        response.setFormattedBankMoney("1,000");
        return response;
    }
}

