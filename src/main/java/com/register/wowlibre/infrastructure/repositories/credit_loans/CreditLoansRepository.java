package com.register.wowlibre.infrastructure.repositories.credit_loans;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface CreditLoansRepository extends CrudRepository<CreditLoansEntity, Long> {

    List<CreditLoansEntity> findByUserId_idAndStatusIsTrue(Long userId);

    Optional<CreditLoansEntity> findByReferenceSerial(String referenceSerial);
}
