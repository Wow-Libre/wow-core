package com.register.wowlibre.repository;

import com.register.wowlibre.infrastructure.entities.AccountGameEntity;
import com.register.wowlibre.infrastructure.entities.CreditLoansEntity;
import com.register.wowlibre.infrastructure.repositories.credit_loans.CreditLoansRepository;
import com.register.wowlibre.infrastructure.repositories.credit_loans.JpaCreditLoansAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaCreditLoansAdapterTest {

    @Mock
    private CreditLoansRepository creditLoansRepository;

    private JpaCreditLoansAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new JpaCreditLoansAdapter(creditLoansRepository);
    }

    @Test
    void findByAccountGameAndStatusIsTrue_shouldReturnList() {
        AccountGameEntity accountGame = createAccountGameEntity(1L);
        CreditLoansEntity loan1 = createCreditLoansEntity(1L, true);
        CreditLoansEntity loan2 = createCreditLoansEntity(2L, true);
        List<CreditLoansEntity> expected = List.of(loan1, loan2);

        when(creditLoansRepository.findByAccountGameIdAndStatusIsTrue(accountGame)).thenReturn(expected);

        List<CreditLoansEntity> result = adapter.findByAccountGameAndStatusIsTrue(accountGame);

        assertEquals(2, result.size());
        assertEquals(expected, result);
        verify(creditLoansRepository).findByAccountGameIdAndStatusIsTrue(accountGame);
    }

    @Test
    void findByAccountGameAndStatusIsTrue_shouldReturnEmptyListWhenNoLoans() {
        AccountGameEntity accountGame = createAccountGameEntity(1L);
        List<CreditLoansEntity> expected = List.of();

        when(creditLoansRepository.findByAccountGameIdAndStatusIsTrue(accountGame)).thenReturn(expected);

        List<CreditLoansEntity> result = adapter.findByAccountGameAndStatusIsTrue(accountGame);

        assertTrue(result.isEmpty());
        verify(creditLoansRepository).findByAccountGameIdAndStatusIsTrue(accountGame);
    }

    @Test
    void findByReferenceSerial_shouldReturnOptionalWhenFound() {
        String referenceSerial = "REF-123";
        CreditLoansEntity expectedEntity = createCreditLoansEntity(1L, true);
        expectedEntity.setReferenceSerial(referenceSerial);

        when(creditLoansRepository.findByReferenceSerial(referenceSerial))
                .thenReturn(Optional.of(expectedEntity));

        Optional<CreditLoansEntity> result = adapter.findByReferenceSerial(referenceSerial);

        assertTrue(result.isPresent());
        assertEquals(referenceSerial, result.get().getReferenceSerial());
        verify(creditLoansRepository).findByReferenceSerial(referenceSerial);
    }

    @Test
    void findByReferenceSerial_shouldReturnEmptyWhenNotFound() {
        String referenceSerial = "REF-999";

        when(creditLoansRepository.findByReferenceSerial(referenceSerial))
                .thenReturn(Optional.empty());

        Optional<CreditLoansEntity> result = adapter.findByReferenceSerial(referenceSerial);

        assertFalse(result.isPresent());
        verify(creditLoansRepository).findByReferenceSerial(referenceSerial);
    }

    @Test
    void creditPendingSend_shouldReturnList() {
        String transactionId = "tx-pending-001";
        CreditLoansEntity loan1 = createCreditLoansEntity(1L, true);
        loan1.setSend(false);
        CreditLoansEntity loan2 = createCreditLoansEntity(2L, true);
        loan2.setSend(false);
        List<CreditLoansEntity> expected = List.of(loan1, loan2);
        Pageable pageable = PageRequest.of(0, 50);
        Page<CreditLoansEntity> pageResult = new PageImpl<>(expected);

        when(creditLoansRepository.findByStatusIsTrueAndSendIsFalse(pageable)).thenReturn(pageResult);

        List<CreditLoansEntity> result = adapter.creditPendingSend(transactionId);

        assertEquals(2, result.size());
        assertEquals(expected, result);
        verify(creditLoansRepository).findByStatusIsTrueAndSendIsFalse(pageable);
    }

    @Test
    void creditPendingSend_shouldReturnEmptyListWhenNoPending() {
        String transactionId = "tx-pending-002";
        Pageable pageable = PageRequest.of(0, 50);
        Page<CreditLoansEntity> pageResult = new PageImpl<>(List.of());

        when(creditLoansRepository.findByStatusIsTrueAndSendIsFalse(pageable)).thenReturn(pageResult);

        List<CreditLoansEntity> result = adapter.creditPendingSend(transactionId);

        assertTrue(result.isEmpty());
        verify(creditLoansRepository).findByStatusIsTrueAndSendIsFalse(pageable);
    }

    @Test
    void creditRequestPending_shouldReturnList() {
        LocalDateTime localDateTime = LocalDateTime.now();
        String transactionId = "tx-request-001";
        CreditLoansEntity loan1 = createCreditLoansEntity(1L, true);
        CreditLoansEntity loan2 = createCreditLoansEntity(2L, true);
        List<CreditLoansEntity> expected = List.of(loan1, loan2);

        when(creditLoansRepository.findActiveCreditLoans(localDateTime)).thenReturn(expected);

        List<CreditLoansEntity> result = adapter.creditRequestPending(localDateTime, transactionId);

        assertEquals(2, result.size());
        assertEquals(expected, result);
        verify(creditLoansRepository).findActiveCreditLoans(localDateTime);
    }

    @Test
    void creditRequestPending_shouldReturnEmptyListWhenNoPending() {
        LocalDateTime localDateTime = LocalDateTime.now();
        String transactionId = "tx-request-002";
        List<CreditLoansEntity> expected = List.of();

        when(creditLoansRepository.findActiveCreditLoans(localDateTime)).thenReturn(expected);

        List<CreditLoansEntity> result = adapter.creditRequestPending(localDateTime, transactionId);

        assertTrue(result.isEmpty());
        verify(creditLoansRepository).findActiveCreditLoans(localDateTime);
    }

    @Test
    void findByRealmIdAndPagination_shouldReturnListWithAscendingSort() {
        Long realmId = 1L;
        int size = 10;
        int page = 0;
        String filter = "ALL";
        boolean asc = true;
        String transactionId = "tx-pagination-001";
        CreditLoansEntity loan1 = createCreditLoansEntity(1L, true);
        CreditLoansEntity loan2 = createCreditLoansEntity(2L, true);
        List<CreditLoansEntity> expected = List.of(loan1, loan2);
        Sort sort = Sort.by(Sort.Order.by("createdAt")).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CreditLoansEntity> pageResult = new PageImpl<>(expected);

        when(creditLoansRepository.findByFilterAndSortAndServerId(filter, realmId, pageable))
                .thenReturn(pageResult);

        List<CreditLoansEntity> result = adapter.findByRealmIdAndPagination(realmId, size, page, filter, asc, transactionId);

        assertEquals(2, result.size());
        assertEquals(expected, result);
        verify(creditLoansRepository).findByFilterAndSortAndServerId(filter, realmId, pageable);
    }

    @Test
    void findByRealmIdAndPagination_shouldReturnListWithDescendingSort() {
        Long realmId = 1L;
        int size = 10;
        int page = 0;
        String filter = "DEBTOR";
        boolean asc = false;
        String transactionId = "tx-pagination-002";
        CreditLoansEntity loan1 = createCreditLoansEntity(1L, true);
        List<CreditLoansEntity> expected = List.of(loan1);
        Sort sort = Sort.by(Sort.Order.by("createdAt")).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CreditLoansEntity> pageResult = new PageImpl<>(expected);

        when(creditLoansRepository.findByFilterAndSortAndServerId(filter, realmId, pageable))
                .thenReturn(pageResult);

        List<CreditLoansEntity> result = adapter.findByRealmIdAndPagination(realmId, size, page, filter, asc, transactionId);

        assertEquals(1, result.size());
        assertEquals(expected, result);
        verify(creditLoansRepository).findByFilterAndSortAndServerId(filter, realmId, pageable);
    }

    @Test
    void findByRealmIdAndPagination_shouldReturnEmptyListWhenNoResults() {
        Long realmId = 1L;
        int size = 10;
        int page = 0;
        String filter = "NON_DEBTOR";
        boolean asc = true;
        String transactionId = "tx-pagination-003";
        Sort sort = Sort.by(Sort.Order.by("createdAt")).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CreditLoansEntity> pageResult = new PageImpl<>(List.of());

        when(creditLoansRepository.findByFilterAndSortAndServerId(filter, realmId, pageable))
                .thenReturn(pageResult);

        List<CreditLoansEntity> result = adapter.findByRealmIdAndPagination(realmId, size, page, filter, asc, transactionId);

        assertTrue(result.isEmpty());
        verify(creditLoansRepository).findByFilterAndSortAndServerId(filter, realmId, pageable);
    }

    @Test
    void save_shouldSaveEntity() {
        String transactionId = "tx-save-001";
        CreditLoansEntity entity = createCreditLoansEntity(1L, true);
        CreditLoansEntity savedEntity = createCreditLoansEntity(1L, true);

        when(creditLoansRepository.save(entity)).thenReturn(savedEntity);

        adapter.save(entity, transactionId);

        verify(creditLoansRepository).save(entity);
    }

    @Test
    void save_shouldHandleNullEntity() {
        String transactionId = "tx-save-002";
        CreditLoansEntity entity = null;

        adapter.save(entity, transactionId);

        verify(creditLoansRepository).save(entity);
    }

    private AccountGameEntity createAccountGameEntity(Long id) {
        AccountGameEntity accountGame = new AccountGameEntity();
        accountGame.setId(id);
        accountGame.setAccountId(id);
        accountGame.setStatus(true);
        return accountGame;
    }

    private CreditLoansEntity createCreditLoansEntity(Long id, boolean status) {
        CreditLoansEntity loan = new CreditLoansEntity();
        loan.setId(id);
        loan.setStatus(status);
        loan.setReferenceSerial("REF-" + id);
        loan.setDebtToPay(100.0);
        loan.setAmountTransferred(500.0);
        loan.setSend(false);
        loan.setPaymentDate(LocalDateTime.now().plusMonths(1));
        return loan;
    }
}

