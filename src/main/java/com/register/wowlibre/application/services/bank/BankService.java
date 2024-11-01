package com.register.wowlibre.application.services.bank;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.*;
import com.register.wowlibre.domain.port.in.account_game.*;
import com.register.wowlibre.domain.port.in.bank.*;
import com.register.wowlibre.domain.port.in.integrator.*;
import com.register.wowlibre.domain.port.in.server_services.*;
import com.register.wowlibre.domain.port.out.credit_loans.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.util.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;

@Service
public class BankService implements BankPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankService.class);
    public static final String BANK_SERVICES_NAME = "BANK";

    private final ObtainCreditLoans obtainCreditLoans;
    private final SaveCreditLoans saveCreditLoans;
    private final IntegratorPort integratorPort;

    private final ServerServicesPort serverServicesPort;
    private final ResourcesPort resourcesPort;

    private final AccountGamePort accountGamePort;
    private final RandomString randomString;

    public BankService(ObtainCreditLoans obtainCreditLoans, SaveCreditLoans saveCreditLoans,
                       IntegratorPort integratorPort, ServerServicesPort serverServicesPort,
                       ResourcesPort resourcesPort, AccountGamePort accountGamePort,
                       @Qualifier("reference-serial-bank") RandomString randomString) {
        this.obtainCreditLoans = obtainCreditLoans;
        this.saveCreditLoans = saveCreditLoans;
        this.integratorPort = integratorPort;
        this.serverServicesPort = serverServicesPort;
        this.resourcesPort = resourcesPort;
        this.accountGamePort = accountGamePort;
        this.randomString = randomString;
    }


    @Override
    public void applyForLoan(Long userId, Long accountId, Long characterId, Long serverId, Long planId,
                             String transactionId) {

        AccountVerificationDto verificationDto = accountGamePort.verifyAccount(userId, accountId, serverId, transactionId);

        ServerEntity server = verificationDto.server();

        if (!server.isStatus()) {
            throw new InternalException("Currently the server is not available to accept loans, contact the " +
                    "administrator.", transactionId);
        }

        ServerServicesModel serverServicesModel = serverServicesPort.findByNameAndServerId(BANK_SERVICES_NAME, serverId,
                transactionId);

        if (serverServicesModel == null) {
            throw new InternalException("The server currently does not have loans configured", transactionId);
        }

        Optional<PlanModel> planSearch =
                resourcesPort.getPlansBank("es", transactionId)
                        .stream().filter(planModel -> planModel.id.equals(planId)).findFirst();

        if (planSearch.isEmpty()) {
            LOGGER.error("The requested plan is not available.");
            throw new InternalException("The requested plan is not available.", transactionId);
        }

        PlanModel plan = planSearch.get();

        if (!obtainCreditLoans.findByUserIdAndStatusIsTrue(userId).isEmpty()) {
            throw new InternalException("You already have a loan.", transactionId);
        }

        int monthPaymentPeriod = plan.monthPaymentPeriod;

        final double cost = calculateMonthlyPayment(plan.gold, plan.interest, monthPaymentPeriod);

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime futurePaymentDate = currentDate.plusMonths(monthPaymentPeriod);

        CreditLoansEntity creditLoansEntity = new CreditLoansEntity();
        creditLoansEntity.setAccountId(accountId);
        creditLoansEntity.setCharacterId(characterId);
        creditLoansEntity.setStatus(true);
        creditLoansEntity.setUserId(verificationDto.accountGame().getUserId());
        creditLoansEntity.setInterests(plan.interest);
        creditLoansEntity.setServerId(serverId);
        creditLoansEntity.setTransactionDate(currentDate);
        creditLoansEntity.setPaymentDate(futurePaymentDate);
        creditLoansEntity.setDebtToPay(cost);
        creditLoansEntity.setAmountTransferred(plan.gold);
        creditLoansEntity.setReferenceSerial(randomString.nextString());
        creditLoansEntity.setSend(false);
        saveCreditLoans.save(creditLoansEntity, transactionId);
    }

    @Override
    public List<ServerAvailableBankDto> serverAvailableLoan(String transactionId) {
        return serverServicesPort.findByServersAvailableLoa(transactionId)
                .stream().map(server -> new ServerAvailableBankDto(server.id(), server.serverName())).toList();
    }

    private double calculateMonthlyPayment(double cost, int interest, int paymentMonth) {
        double interestRate = interest / 100.0;
        double monthlyInterest = cost * interestRate;
        return (cost / paymentMonth) + monthlyInterest;
    }
}
