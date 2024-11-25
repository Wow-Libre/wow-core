package com.register.wowlibre.domain.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.io.*;
import java.util.*;

@Data
public class MailModel implements Serializable {
    private Long id;
    @JsonProperty("message_type")
    private Long messageType;
    @JsonProperty("sender_guid_id")
    private Long senderGuidId;
    @JsonProperty("sender_name")
    private String senderName;
    private String subject;
    private String body;
    @JsonProperty("has_items")
    private boolean hasItems;
    @JsonProperty("expire_time")
    private Date expireTime;
    @JsonProperty("deliver_time")
    private Date deliverTime;
    private Integer money;
    private List<Items> items;


    @Data
    @AllArgsConstructor
    public static class Items implements Serializable {
        @JsonProperty("mail_id")
        private Long mailId;
        @JsonProperty("item_id")
        private Long itemId;
        private Long duration;
        @JsonProperty("item_instance_id")
        private Long itemInstanceId;
    }
}
