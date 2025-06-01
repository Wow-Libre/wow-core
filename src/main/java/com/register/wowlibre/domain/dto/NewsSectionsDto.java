package com.register.wowlibre.domain.dto;

import lombok.*;

import java.time.*;
import java.util.*;

@Data
public class NewsSectionsDto {
    public String title;
    public String subTitle;
    public String imgUrl;
    public String author;
    public LocalDateTime createdAt;
    public List<Section> sections;


    public record Section(Long id, String title, String content, String imgUrl, Integer sectionOrder) {
    }

}
