package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "news_sections")
public class NewsSectionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "img_url")
    private String imgUrl;
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "section_order")
    private Integer sectionOrder;
    @JoinColumn(
            name = "news_id",
            referencedColumnName = "id")
    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY)
    private NewsEntity newsId;
}
