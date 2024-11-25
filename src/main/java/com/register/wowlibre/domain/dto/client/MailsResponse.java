package com.register.wowlibre.domain.dto.client;

import com.register.wowlibre.domain.model.*;
import lombok.*;

import java.util.*;

@Data
public class MailsResponse {
    private List<MailModel> mails;
    private int size;
}
