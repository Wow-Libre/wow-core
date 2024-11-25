package com.register.wowlibre.domain.dto;

import com.register.wowlibre.domain.model.*;
import lombok.*;

import java.io.*;
import java.util.*;

@Data
@AllArgsConstructor
public class MailsDto implements Serializable {
    private List<MailModel> mails;
    private int size;
}
