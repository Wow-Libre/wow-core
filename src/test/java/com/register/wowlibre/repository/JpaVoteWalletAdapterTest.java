package com.register.wowlibre.repository;

import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.repositories.vote_wallet.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaVoteWalletAdapterTest {

    private VoteWalletRepository voteWalletRepository;
    private JpaVoteWalletAdapter adapter;

    @BeforeEach
    void setUp() {
        voteWalletRepository = mock(VoteWalletRepository.class);
        adapter = new JpaVoteWalletAdapter(voteWalletRepository);
    }

    @Test
    void findByReferenceCode_returnsOptional() {
        VoteWalletEntity entity = new VoteWalletEntity();
        when(voteWalletRepository.findByReferenceCode("ref")).thenReturn(Optional.of(entity));

        Optional<VoteWalletEntity> result = adapter.findByReferenceCode("ref", "tx");

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
    }

    @Test
    void findByReferenceCode_notFound_returnsEmpty() {
        when(voteWalletRepository.findByReferenceCode("ref")).thenReturn(Optional.empty());

        Optional<VoteWalletEntity> result = adapter.findByReferenceCode("ref", "tx");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserIdAndPlatformId_returnsOptional() {
        VoteWalletEntity entity = new VoteWalletEntity();
        when(voteWalletRepository.findByUserId_IdAndPlatformId_Id(1L, 2L)).thenReturn(Optional.of(entity));

        Optional<VoteWalletEntity> result = adapter.findByUserIdAndPlatformId(1L, 2L);

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
    }

    @Test
    void findByUserIdAndPlatformId_notFound_returnsEmpty() {
        when(voteWalletRepository.findByUserId_IdAndPlatformId_Id(1L, 2L)).thenReturn(Optional.empty());

        Optional<VoteWalletEntity> result = adapter.findByUserIdAndPlatformId(1L, 2L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserId_returnsList() {
        VoteWalletEntity entity = new VoteWalletEntity();
        when(voteWalletRepository.findByUserId_Id(1L)).thenReturn(List.of(entity));

        List<VoteWalletEntity> result = adapter.findByUserId(1L, "tx");

        assertEquals(1, result.size());
        assertEquals(entity, result.get(0));
    }

    @Test
    void saveVoteWallet_callsRepositorySave() {
        VoteWalletEntity entity = new VoteWalletEntity();

        adapter.saveVoteWallet(entity, "tx");

        verify(voteWalletRepository).save(entity);
    }
}
