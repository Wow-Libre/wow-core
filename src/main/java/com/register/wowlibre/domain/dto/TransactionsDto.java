package com.register.wowlibre.domain.dto;

import com.register.wowlibre.domain.model.*;
import lombok.*;

import java.util.*;

@Data
public class TransactionsDto {
    private List<Transaction> transactions;
    private Long size;
}
