package com.register.wowlibre.service;

import com.register.wowlibre.application.services.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.port.in.user.*;
import com.register.wowlibre.domain.port.out.vote_wallet.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VoteWalletServiceTest {
    private ObtainVoteWallet obtainVoteWallet;
    private SaveVoteWallet saveVoteWallet;
    private UserPort userPort;
    private VoteWalletService service;

    @BeforeEach
    void setUp() {
        obtainVoteWallet = mock(ObtainVoteWallet.class);
        saveVoteWallet = mock(SaveVoteWallet.class);
        userPort = mock(UserPort.class);
        service = new VoteWalletService(obtainVoteWallet, saveVoteWallet, userPort);
    }

    @Test
    void findByReferenceCode_found_returnsEntity() {
        VoteWalletEntity entity = new VoteWalletEntity();
        when(obtainVoteWallet.findByReferenceCode("ref", "tx")).thenReturn(Optional.of(entity));

        VoteWalletEntity result = service.findByReferenceCode("ref", "tx");

        assertEquals(entity, result);
    }

    @Test
    void findByReferenceCode_notFound_throwsException() {
        when(obtainVoteWallet.findByReferenceCode("ref", "tx")).thenReturn(Optional.empty());

        assertThrows(InternalException.class, () -> service.findByReferenceCode("ref", "tx"));
    }

    @Test
    void saveVoteWallet_callsSave() {
        VoteWalletEntity entity = new VoteWalletEntity();

        service.saveVoteWallet(entity, "tx");

        verify(saveVoteWallet).saveVoteWallet(entity, "tx");
    }

    @Test
    void create_userExistsAndActive_savesWallet() {
        UserEntity user = new UserEntity();
        user.setStatus(true);
        when(userPort.findByUserId(1L, "tx")).thenReturn(Optional.of(user));
        VotingPlatformsEntity platform = new VotingPlatformsEntity();

        service.create(1L, platform, "ref", "tx");

        verify(saveVoteWallet).saveVoteWallet(any(VoteWalletEntity.class), eq("tx"));
    }

    @Test
    void create_userNotFound_throwsException() {
        when(userPort.findByUserId(1L, "tx")).thenReturn(Optional.empty());
        VotingPlatformsEntity platform = new VotingPlatformsEntity();

        assertThrows(InternalException.class, () -> service.create(1L, platform, "ref", "tx"));
    }

    @Test
    void create_userInactive_throwsException() {
        UserEntity user = new UserEntity();
        user.setStatus(false);
        when(userPort.findByUserId(1L, "tx")).thenReturn(Optional.of(user));
        VotingPlatformsEntity platform = new VotingPlatformsEntity();

        assertThrows(InternalException.class, () -> service.create(1L, platform, "ref", "tx"));
    }

    @Test
    void findByUserIdAndPlatformId_delegatesToObtainVoteWallet() {
        Optional<VoteWalletEntity> expected = Optional.of(new VoteWalletEntity());
        when(obtainVoteWallet.findByUserIdAndPlatformId(1L, 2L)).thenReturn(expected);

        Optional<VoteWalletEntity> result = service.findByUserIdAndPlatformId(1L, 2L);

        assertEquals(expected, result);
    }

    @Test
    void findByUserId_delegatesToObtainVoteWallet() {
        List<VoteWalletEntity> expected = List.of(new VoteWalletEntity());
        when(obtainVoteWallet.findByUserId(1L, "tx")).thenReturn(expected);

        List<VoteWalletEntity> result = service.findByUserId(1L, "tx");

        assertEquals(expected, result);
    }
}
