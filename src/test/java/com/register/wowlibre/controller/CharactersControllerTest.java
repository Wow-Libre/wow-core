package com.register.wowlibre.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.port.in.characters.CharactersPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.infrastructure.controller.CharactersController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CharactersControllerTest {

    @Mock
    private CharactersPort charactersPort;

    @InjectMocks
    private CharactersController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnCharacters() {
        long userId = 1L;
        long accountId = 101L;
        long serverId = 1L;
        String transactionId = "tx-char-001";
        CharactersDto charactersDto = new CharactersDto();
        charactersDto.setCharacters(new ArrayList<>());
        charactersDto.setTotalQuantity(0);

        when(charactersPort.characters(userId, accountId, serverId, transactionId)).thenReturn(charactersDto);

        ResponseEntity<GenericResponse<CharactersDto>> response = controller.characters(
                transactionId, accountId, serverId, userId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(charactersPort).characters(userId, accountId, serverId, transactionId);
    }

    @Test
    void shouldReturnNoContentWhenCharactersIsNull() {
        long userId = 1L;
        long accountId = 101L;
        long serverId = 1L;
        String transactionId = "tx-char-002";

        when(charactersPort.characters(userId, accountId, serverId, transactionId)).thenReturn(null);

        ResponseEntity<GenericResponse<CharactersDto>> response = controller.characters(
                transactionId, accountId, serverId, userId
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(charactersPort).characters(userId, accountId, serverId, transactionId);
    }

    @Test
    void shouldReturnLoanApplicationCharacters() {
        long userId = 1L;
        long accountId = 101L;
        long serverId = 1L;
        String transactionId = "tx-char-003";
        CharactersDto charactersDto = new CharactersDto();
        charactersDto.setCharacters(new ArrayList<>());
        charactersDto.setTotalQuantity(0);

        when(charactersPort.loanApplicationCharacters(userId, accountId, serverId, transactionId))
                .thenReturn(charactersDto);

        ResponseEntity<GenericResponse<CharactersDto>> response = controller.loanApplicationCharacters(
                transactionId, accountId, serverId, userId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(charactersPort).loanApplicationCharacters(userId, accountId, serverId, transactionId);
    }

    @Test
    void shouldReturnNoContentWhenLoanApplicationCharactersIsNull() {
        long userId = 1L;
        long accountId = 101L;
        long serverId = 1L;
        String transactionId = "tx-char-004";

        when(charactersPort.loanApplicationCharacters(userId, accountId, serverId, transactionId))
                .thenReturn(null);

        ResponseEntity<GenericResponse<CharactersDto>> response = controller.loanApplicationCharacters(
                transactionId, accountId, serverId, userId
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(charactersPort).loanApplicationCharacters(userId, accountId, serverId, transactionId);
    }

    @Test
    void shouldDeleteFriend() {
        long userId = 1L;
        String transactionId = "tx-char-005";
        DeleteFriendDto deleteFriendDto = new DeleteFriendDto();
        deleteFriendDto.setAccountId(101L);
        deleteFriendDto.setServerId(1L);
        deleteFriendDto.setCharacterId(201L);
        deleteFriendDto.setFriendId(301L);

        ResponseEntity<GenericResponse<Void>> response = controller.deleteFriend(
                transactionId, userId, deleteFriendDto
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(charactersPort).deleteFriend(userId, deleteFriendDto.getAccountId(),
                deleteFriendDto.getServerId(), deleteFriendDto.getCharacterId(),
                deleteFriendDto.getFriendId(), transactionId);
    }

    @Test
    void shouldReturnMails() {
        long userId = 1L;
        long accountId = 101L;
        long serverId = 1L;
        long characterId = 201L;
        String transactionId = "tx-char-006";
        MailsDto mailsDto = new MailsDto(new ArrayList<>(), 0);

        when(charactersPort.mails(userId, accountId, serverId, characterId, transactionId))
                .thenReturn(mailsDto);

        ResponseEntity<GenericResponse<MailsDto>> response = controller.mails(
                transactionId, userId, accountId, serverId, characterId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(charactersPort).mails(userId, accountId, serverId, characterId, transactionId);
    }

    @Test
    void shouldReturnNoContentWhenMailsIsNull() {
        long userId = 1L;
        long accountId = 101L;
        long serverId = 1L;
        long characterId = 201L;
        String transactionId = "tx-char-007";

        when(charactersPort.mails(userId, accountId, serverId, characterId, transactionId))
                .thenReturn(null);

        ResponseEntity<GenericResponse<MailsDto>> response = controller.mails(
                transactionId, userId, accountId, serverId, characterId
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(charactersPort).mails(userId, accountId, serverId, characterId, transactionId);
    }

    @Test
    void shouldReturnFriends() {
        long userId = 1L;
        long accountId = 101L;
        long serverId = 1L;
        long characterId = 201L;
        String transactionId = "tx-char-008";
        CharacterSocialDto characterSocialDto = new CharacterSocialDto(new ArrayList<>(), 0);

        when(charactersPort.friends(userId, accountId, serverId, characterId, transactionId))
                .thenReturn(characterSocialDto);

        ResponseEntity<GenericResponse<CharacterSocialDto>> response = controller.friends(
                transactionId, userId, accountId, serverId, characterId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(charactersPort).friends(userId, accountId, serverId, characterId, transactionId);
    }

    @Test
    void shouldReturnNoContentWhenFriendsIsNull() {
        long userId = 1L;
        long accountId = 101L;
        long serverId = 1L;
        long characterId = 201L;
        String transactionId = "tx-char-009";

        when(charactersPort.friends(userId, accountId, serverId, characterId, transactionId))
                .thenReturn(null);

        ResponseEntity<GenericResponse<CharacterSocialDto>> response = controller.friends(
                transactionId, userId, accountId, serverId, characterId
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(charactersPort).friends(userId, accountId, serverId, characterId, transactionId);
    }

    @Test
    void shouldChangePassword() {
        long userId = 1L;
        String transactionId = "tx-char-010";
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setAccountId(101L);
        changePasswordDto.setServerId(1L);
        changePasswordDto.setPassword("oldPassword");
        changePasswordDto.setNewPassword("newPassword");

        ResponseEntity<GenericResponse<Void>> response = controller.changePassword(
                transactionId, userId, changePasswordDto
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(charactersPort).changePassword(userId, changePasswordDto.getAccountId(),
                changePasswordDto.getServerId(), changePasswordDto.getPassword(),
                changePasswordDto.getNewPassword(), transactionId);
    }

    @Test
    void shouldReturnProfessions() {
        long userId = 1L;
        long accountId = 101L;
        long serverId = 1L;
        long characterId = 201L;
        String transactionId = "tx-char-011";
        List<CharacterProfessionsDto> professions = new ArrayList<>();

        when(charactersPort.professions(userId, accountId, serverId, characterId, transactionId))
                .thenReturn(professions);

        ResponseEntity<GenericResponse<List<CharacterProfessionsDto>>> response = controller.professions(
                transactionId, userId, accountId, serverId, characterId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(charactersPort).professions(userId, accountId, serverId, characterId, transactionId);
    }

    @Test
    void shouldSendLevel() {
        long userId = 1L;
        String transactionId = "tx-char-012";
        SendLevelDto sendLevelDto = new SendLevelDto();
        sendLevelDto.setAccountId(101L);
        sendLevelDto.setServerId(1L);
        sendLevelDto.setCharacterId(201L);
        sendLevelDto.setFriendId(301L);
        sendLevelDto.setLevel(10);

        ResponseEntity<GenericResponse<Void>> response = controller.sendLevel(
                transactionId, userId, sendLevelDto
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(charactersPort).sendLevel(userId, sendLevelDto.getAccountId(),
                sendLevelDto.getServerId(), sendLevelDto.getCharacterId(),
                sendLevelDto.getFriendId(), sendLevelDto.getLevel(), transactionId);
    }

    @Test
    void shouldSendMoney() {
        long userId = 1L;
        String transactionId = "tx-char-013";
        SendMoneyDto sendMoneyDto = new SendMoneyDto();
        sendMoneyDto.setAccountId(101L);
        sendMoneyDto.setServerId(1L);
        sendMoneyDto.setCharacterId(201L);
        sendMoneyDto.setFriendId(301L);
        sendMoneyDto.setMoney(1000L);

        ResponseEntity<GenericResponse<Void>> response = controller.sendMoney(
                transactionId, userId, sendMoneyDto
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(charactersPort).sendMoney(userId, sendMoneyDto.getAccountId(),
                sendMoneyDto.getServerId(), sendMoneyDto.getCharacterId(),
                sendMoneyDto.getFriendId(), sendMoneyDto.getMoney(), transactionId);
    }

    @Test
    void shouldSendAnnouncement() {
        long userId = 1L;
        String transactionId = "tx-char-014";
        AnnouncementDto announcementDto = new AnnouncementDto(101L, 201L, 401L, "Test message", 1L);

        ResponseEntity<GenericResponse<Void>> response = controller.announcementProfession(
                transactionId, userId, announcementDto
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(charactersPort).sendAnnouncement(userId, announcementDto.getAccountId(),
                announcementDto.getServerId(), announcementDto.getCharacterId(),
                announcementDto.getSkillId(), announcementDto.getMessage(), transactionId);
    }

    @Test
    void shouldReturnInventory() {
        long userId = 1L;
        long accountId = 101L;
        long serverId = 1L;
        long characterId = 201L;
        String transactionId = "tx-char-015";
        List<CharacterInventoryResponse> inventory = new ArrayList<>();

        when(charactersPort.getCharacterInventory(userId, accountId, serverId, characterId, transactionId))
                .thenReturn(inventory);

        ResponseEntity<GenericResponse<List<CharacterInventoryResponse>>> response = controller.inventory(
                transactionId, userId, accountId, serverId, characterId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(charactersPort).getCharacterInventory(userId, accountId, serverId, characterId, transactionId);
    }

    @Test
    void shouldTransferInventoryItem() {
        long userId = 1L;
        String transactionId = "tx-char-016";
        TransferItemDto transferItemDto = new TransferItemDto();
        transferItemDto.setAccountId(101L);
        transferItemDto.setServerId(1L);
        transferItemDto.setCharacterId(201L);
        transferItemDto.setFriendId(301L);
        transferItemDto.setItemId(401L);
        transferItemDto.setCount(1);

        ResponseEntity<GenericResponse<Void>> response = controller.transferInventoryItem(
                transactionId, userId, transferItemDto
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(charactersPort).transferInventoryItem(userId, transferItemDto.getAccountId(),
                transferItemDto.getServerId(), transferItemDto.getCharacterId(),
                transferItemDto.getFriendId(), transferItemDto.getCount(),
                transferItemDto.getItemId(), transactionId);
    }
}

