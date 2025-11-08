package com.register.wowlibre.repository;

import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.repositories.account_game.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaAccountGameAdapterTest {
    @Mock
    private AccountGameRepository accountGameRepository;

    private JpaAccountGameAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new JpaAccountGameAdapter(accountGameRepository);
    }

    @Test
    void findByUserIdAndStatusIsTrue_shouldReturnListFromPage() {
        long userId = 1L;
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        List<AccountGameEntity> entities = List.of(new AccountGameEntity());
        Page<AccountGameEntity> pageResult = new PageImpl<>(entities);

        when(accountGameRepository.findByUserId_IdAndStatusIsTrue(userId, pageable)).thenReturn(pageResult);

        List<AccountGameEntity> result = adapter.findByUserIdAndStatusIsTrue(userId, page, size, "tx123");

        assertEquals(1, result.size());
        verify(accountGameRepository).findByUserId_IdAndStatusIsTrue(userId, pageable);
    }

    @Test
    void findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue_shouldReturnOptional() {
        long userId = 1L, accountId = 2L, realmId = 3L;
        Optional<AccountGameEntity> expected = Optional.of(new AccountGameEntity());

        when(accountGameRepository.findByUserId_IdAndAccountIdAndRealmId_idAndStatusIsTrue(userId, accountId, realmId))
                .thenReturn(expected);

        Optional<AccountGameEntity> result = adapter.findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(
                userId, accountId, realmId, "tx123");

        assertTrue(result.isPresent());
        verify(accountGameRepository).findByUserId_IdAndAccountIdAndRealmId_idAndStatusIsTrue(userId, accountId, realmId);
    }

    @Test
    void accounts_shouldReturnCount() {
        long userId = 1L;
        when(accountGameRepository.countByUserId(userId)).thenReturn(5L);

        Long result = adapter.accounts(userId);

        assertEquals(5L, result);
        verify(accountGameRepository).countByUserId(userId);
    }

    @Test
    void findByUserIdAndRealmNameAndUsernameStatusIsTrue_shouldReturnListFromPage() {
        long userId = 1L;
        int page = 0, size = 5;
        String realmName = "RealmA";
        String username = "user123";
        Pageable pageable = PageRequest.of(page, size);
        List<AccountGameEntity> entities = List.of(new AccountGameEntity());
        Page<AccountGameEntity> pageResult = new PageImpl<>(entities);

        when(accountGameRepository.findByUserId_IdAndStatusIsTrueAndRealmNameAndUsername(
                realmName, userId, username, pageable)).thenReturn(pageResult);

        List<AccountGameEntity> result = adapter.findByUserIdAndRealmNameAndUsernameStatusIsTrue(
                userId, page, size, realmName, username, "tx123");

        assertEquals(1, result.size());
        verify(accountGameRepository).findByUserId_IdAndStatusIsTrueAndRealmNameAndUsername(
                realmName, userId, username, pageable);
    }

    @Test
    void findByUserIdAndRealmId_shouldReturnList() {
        long userId = 1L, realmId = 99L;
        List<AccountGameEntity> expected = List.of(new AccountGameEntity());

        when(accountGameRepository.findByUserId_IdAndRealmId_IdAndStatusIsTrue(userId, realmId))
                .thenReturn(expected);

        List<AccountGameEntity> result = adapter.findByUserIdAndRealmId(userId, realmId, "tx123");

        assertEquals(1, result.size());
        verify(accountGameRepository).findByUserId_IdAndRealmId_IdAndStatusIsTrue(userId, realmId);
    }

    @Test
    void findByIdAndUserId_shouldReturnOptionalWhenFound() {
        long id = 100L;
        long userId = 1L;
        String transactionId = "tx-find-001";
        AccountGameEntity expectedEntity = new AccountGameEntity();
        expectedEntity.setId(id);

        when(accountGameRepository.findByIdAndUserId_Id(id, userId))
                .thenReturn(Optional.of(expectedEntity));

        Optional<AccountGameEntity> result = adapter.findByIdAndUserId(id, userId, transactionId);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(accountGameRepository).findByIdAndUserId_Id(id, userId);
    }

    @Test
    void findByIdAndUserId_shouldReturnEmptyWhenNotFound() {
        long id = 100L;
        long userId = 1L;
        String transactionId = "tx-find-002";

        when(accountGameRepository.findByIdAndUserId_Id(id, userId))
                .thenReturn(Optional.empty());

        Optional<AccountGameEntity> result = adapter.findByIdAndUserId(id, userId, transactionId);

        assertFalse(result.isPresent());
        verify(accountGameRepository).findByIdAndUserId_Id(id, userId);
    }

    @Test
    void save_shouldReturnSavedEntity() {
        AccountGameEntity entity = new AccountGameEntity();
        when(accountGameRepository.save(entity)).thenReturn(entity);

        AccountGameEntity result = adapter.save(entity, "tx123");

        assertEquals(entity, result);
        verify(accountGameRepository).save(entity);
    }

    @Test
    void countActiveAccountsByUserId_shouldReturnCount() {
        long userId = 1L;
        long expectedCount = 5L;
        String transactionId = "tx-count-001";

        when(accountGameRepository.countActiveAccountsByUserId(userId)).thenReturn(expectedCount);

        long result = adapter.countActiveAccountsByUserId(userId, transactionId);

        assertEquals(expectedCount, result);
        verify(accountGameRepository).countActiveAccountsByUserId(userId);
    }

    @Test
    void countActiveAccountsByUserId_shouldReturnZeroWhenNoAccounts() {
        long userId = 1L;
        long expectedCount = 0L;
        String transactionId = "tx-count-002";

        when(accountGameRepository.countActiveAccountsByUserId(userId)).thenReturn(expectedCount);

        long result = adapter.countActiveAccountsByUserId(userId, transactionId);

        assertEquals(0L, result);
        verify(accountGameRepository).countActiveAccountsByUserId(userId);
    }

    @Test
    void countDistinctRealmsByUserId_shouldReturnCount() {
        long userId = 1L;
        long expectedCount = 3L;
        String transactionId = "tx-realms-001";

        when(accountGameRepository.countDistinctRealmsByUserId(userId)).thenReturn(expectedCount);

        long result = adapter.countDistinctRealmsByUserId(userId, transactionId);

        assertEquals(expectedCount, result);
        verify(accountGameRepository).countDistinctRealmsByUserId(userId);
    }

    @Test
    void countDistinctRealmsByUserId_shouldReturnZeroWhenNoRealms() {
        long userId = 1L;
        long expectedCount = 0L;
        String transactionId = "tx-realms-002";

        when(accountGameRepository.countDistinctRealmsByUserId(userId)).thenReturn(expectedCount);

        long result = adapter.countDistinctRealmsByUserId(userId, transactionId);

        assertEquals(0L, result);
        verify(accountGameRepository).countDistinctRealmsByUserId(userId);
    }
}
