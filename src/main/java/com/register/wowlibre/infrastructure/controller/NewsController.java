package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.*;
import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.port.in.news.*;
import com.register.wowlibre.domain.shared.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsPort newsPort;

    public NewsController(NewsPort newsPort) {
        this.newsPort = newsPort;
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<NewsSummaryDto>>> news(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestParam final int size,
            @RequestParam final int page) {

        List<NewsSummaryDto> news = newsPort.news(size, page, transactionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(news, transactionId).ok().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<NewsSectionsDto>> detail(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @PathVariable Long id) {

        NewsSectionsDto news = newsPort.newsSectionId(id, transactionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(news, transactionId).ok().build());
    }

    @PostMapping
    public ResponseEntity<GenericResponse<Void>> saveNews(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestBody CreateNewsDto createNewsDto) {

        newsPort.saveNews(createNewsDto, transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(null, transactionId).ok().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<Void>> deleteNews(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @PathVariable Long id) {

        newsPort.deleteNews(id, transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(null, transactionId).ok().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<Void>> updateNews(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @PathVariable Long id,
            @RequestBody UpdateNewsDto updateNewsDto) {

        newsPort.updateNews(id, updateNewsDto, transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(null, transactionId).ok().build());
    }

    @PostMapping("/{newsId}/sections")
    public ResponseEntity<GenericResponse<Void>> createNewsSection(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @PathVariable Long newsId,
            @RequestBody CreateNewsSectionDto createNewsSectionDto) {

        newsPort.createNewsSection(newsId, createNewsSectionDto, transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(null, transactionId).ok().build());
    }

    @DeleteMapping("/{newsId}/sections/{sectionId}")
    public ResponseEntity<GenericResponse<Void>> deleteNewsSection(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @PathVariable Long newsId,
            @PathVariable Long sectionId) {

        newsPort.deleteNewsSection(newsId, sectionId, transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(null, transactionId).ok().build());
    }
}
