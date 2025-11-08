package com.register.wowlibre.controller;

import com.register.wowlibre.domain.dto.NewsSectionsDto;
import com.register.wowlibre.domain.dto.NewsSummaryDto;
import com.register.wowlibre.domain.dto.CreateNewsDto;
import com.register.wowlibre.domain.UpdateNewsDto;
import com.register.wowlibre.domain.dto.CreateNewsSectionDto;
import com.register.wowlibre.domain.port.in.news.NewsPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.infrastructure.controller.NewsController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NewsControllerTest {

    @Mock
    private NewsPort newsPort;

    @InjectMocks
    private NewsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnNewsList() {
        String transactionId = "tx-news-001";
        int size = 10;
        int page = 0;
        NewsSummaryDto news1 = new NewsSummaryDto();
        List<NewsSummaryDto> news = List.of(news1);

        when(newsPort.news(size, page, transactionId)).thenReturn(news);

        ResponseEntity<GenericResponse<List<NewsSummaryDto>>> response = controller.news(transactionId, size, page);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
        verify(newsPort).news(size, page, transactionId);
    }

    @Test
    void shouldReturnNewsDetail() {
        String transactionId = "tx-news-002";
        long id = 1L;
        NewsSectionsDto news = new NewsSectionsDto();

        when(newsPort.newsSectionId(id, transactionId)).thenReturn(news);

        ResponseEntity<GenericResponse<NewsSectionsDto>> response = controller.detail(transactionId, id);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(newsPort).newsSectionId(id, transactionId);
    }

    @Test
    void shouldSaveNews() {
        String transactionId = "tx-news-003";
        CreateNewsDto createNewsDto = new CreateNewsDto();

        ResponseEntity<GenericResponse<Void>> response = controller.saveNews(transactionId, createNewsDto);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(newsPort).saveNews(createNewsDto, transactionId);
    }

    @Test
    void shouldDeleteNews() {
        String transactionId = "tx-news-004";
        long id = 1L;

        ResponseEntity<GenericResponse<Void>> response = controller.deleteNews(transactionId, id);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(newsPort).deleteNews(id, transactionId);
    }

    @Test
    void shouldUpdateNews() {
        String transactionId = "tx-news-005";
        long id = 1L;
        UpdateNewsDto updateNewsDto = new UpdateNewsDto();

        ResponseEntity<GenericResponse<Void>> response = controller.updateNews(transactionId, id, updateNewsDto);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(newsPort).updateNews(id, updateNewsDto, transactionId);
    }

    @Test
    void shouldCreateNewsSection() {
        String transactionId = "tx-news-006";
        long newsId = 1L;
        CreateNewsSectionDto createNewsSectionDto = new CreateNewsSectionDto();

        ResponseEntity<GenericResponse<Void>> response = controller.createNewsSection(transactionId, newsId, createNewsSectionDto);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(newsPort).createNewsSection(newsId, createNewsSectionDto, transactionId);
    }

    @Test
    void shouldDeleteNewsSection() {
        String transactionId = "tx-news-007";
        long newsId = 1L;
        long sectionId = 1L;

        ResponseEntity<GenericResponse<Void>> response = controller.deleteNewsSection(transactionId, newsId, sectionId);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(newsPort).deleteNewsSection(newsId, sectionId, transactionId);
    }
}

