package com.register.wowlibre.service;

import com.register.wowlibre.application.services.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.vote_wallet.*;
import com.register.wowlibre.domain.port.out.voting_platforms.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.util.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VotingPlatformsServiceTest {

    private ObtainVotingPlatforms obtainVotingPlatforms;
    private SaveVotingPlatForms saveVotingPlatformPort;
    private VoteWalletPort voteWalletPort;
    private RandomString randomString;
    private VotingPlatformsService service;

    @BeforeEach
    void setUp() {
        obtainVotingPlatforms = mock(ObtainVotingPlatforms.class);
        saveVotingPlatformPort = mock(SaveVotingPlatForms.class);
        voteWalletPort = mock(VoteWalletPort.class);
        randomString = mock(RandomString.class);
        service = new VotingPlatformsService(obtainVotingPlatforms, saveVotingPlatformPort, voteWalletPort,
                randomString);
    }

    @Test
    void findAllActiveVotingPlatforms_returnsMappedList() {
        VotingPlatformsEntity entity = new VotingPlatformsEntity();
        entity.setId(1L);
        entity.setImgUrl("img");
        entity.setName("name");
        entity.setPostbackUrl("url/changeme");
        when(obtainVotingPlatforms.findAllActiveVotingPlatforms()).thenReturn(List.of(entity));
        when(randomString.nextString()).thenReturn("refcode");
        when(voteWalletPort.findByUserIdAndPlatformId(any(), any())).thenReturn(Optional.empty());

        List<VotingPlatformsModel> result = service.findAllActiveVotingPlatforms(2L, "tx");

        assertEquals(1, result.size());
        assertEquals("name", result.get(0).name());
    }

    @Test
    void createVotingPlatform_savesEntity() {
        service.createVotingPlatform("n", "img", "post", "host", "tx");
        verify(saveVotingPlatformPort).save(any(VotingPlatformsEntity.class));
    }

    @Test
    void updateVotingPlatform_notFound_throwsException() {
        when(obtainVotingPlatforms.findById(1L)).thenReturn(Optional.empty());
        assertThrows(InternalException.class, () -> service.updateVotingPlatform(1L, "n", "i", "p", "h", "tx"));
    }

    @Test
    void updateVotingPlatform_found_updatesAndSaves() {
        VotingPlatformsEntity entity = new VotingPlatformsEntity();
        when(obtainVotingPlatforms.findById(1L)).thenReturn(Optional.of(entity));
        service.updateVotingPlatform(1L, "n", "i", "p", "h", "tx");
        verify(saveVotingPlatformPort).save(entity);
        assertEquals("n", entity.getName());
    }

    @Test
    void deleteVotingPlatform_notFound_throwsException() {
        when(obtainVotingPlatforms.findById(1L)).thenReturn(Optional.empty());
        assertThrows(InternalException.class, () -> service.deleteVotingPlatform(1L, "tx"));
    }

    @Test
    void deleteVotingPlatform_found_setsInactiveAndRandomName() {
        VotingPlatformsEntity entity = new VotingPlatformsEntity();
        when(obtainVotingPlatforms.findById(1L)).thenReturn(Optional.of(entity));
        when(randomString.nextString()).thenReturn("random");
        service.deleteVotingPlatform(1L, "tx");
        assertFalse(entity.isActive());
        assertEquals("random", entity.getName());
        verify(saveVotingPlatformPort).save(entity);
    }

    @Test
    void postbackVotingPlatform_invalidCode_throwsException() {
        assertThrows(InternalException.class, () -> service.postbackVotingPlatform("-1200", "tx"));
    }

    @Test
    void postbackVotingPlatform_validCode_updatesWallet() {
        VoteWalletEntity wallet = new VoteWalletEntity();
        wallet.setVoteBalance(2);
        wallet.setTotalVotes(2);
        when(voteWalletPort.findByReferenceCode("ref", "tx")).thenReturn(wallet);

        service.postbackVotingPlatform("ref-11", "tx");

        assertEquals(3, wallet.getVoteBalance());
        assertEquals(3, wallet.getTotalVotes());
        verify(voteWalletPort).saveVoteWallet(wallet, "tx");
    }

    @Test
    void votes_sumsVoteBalance() {
        VoteWalletEntity w1 = new VoteWalletEntity();
        w1.setVoteBalance(2);
        VoteWalletEntity w2 = new VoteWalletEntity();
        w2.setVoteBalance(3);
        when(voteWalletPort.findByUserId(1L, "tx")).thenReturn(List.of(w1, w2));

        Integer total = service.votes(1L, "tx");

        assertEquals(5, total);
    }
}
