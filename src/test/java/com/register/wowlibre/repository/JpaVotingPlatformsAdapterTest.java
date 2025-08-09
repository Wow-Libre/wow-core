package com.register.wowlibre.repository;

import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.repositories.voting_platforms.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaVotingPlatformsAdapterTest {

    private VotingPlatformsRepository votingPlatformsRepository;
    private JpaVotingPlatformsAdapter adapter;

    @BeforeEach
    void setUp() {
        votingPlatformsRepository = mock(VotingPlatformsRepository.class);
        adapter = new JpaVotingPlatformsAdapter(votingPlatformsRepository);
    }

    @Test
    void findAllActiveVotingPlatforms_returnsList() {
        VotingPlatformsEntity entity = new VotingPlatformsEntity();
        when(votingPlatformsRepository.findAllByIsActiveTrue()).thenReturn(List.of(entity));

        List<VotingPlatformsEntity> result = adapter.findAllActiveVotingPlatforms();

        assertEquals(1, result.size());
        assertEquals(entity, result.get(0));
    }

    @Test
    void findById_found_returnsOptionalWithEntity() {
        VotingPlatformsEntity entity = new VotingPlatformsEntity();
        when(votingPlatformsRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<VotingPlatformsEntity> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
    }

    @Test
    void findById_notFound_returnsEmptyOptional() {
        when(votingPlatformsRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<VotingPlatformsEntity> result = adapter.findById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void save_callsRepositorySave() {
        VotingPlatformsEntity entity = new VotingPlatformsEntity();

        adapter.save(entity);

        verify(votingPlatformsRepository).save(entity);
    }
}
