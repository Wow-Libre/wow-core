package com.register.wowlibre.service;

import com.register.wowlibre.application.services.news.NewsService;
import com.register.wowlibre.domain.UpdateNewsDto;
import com.register.wowlibre.domain.dto.CreateNewsDto;
import com.register.wowlibre.domain.dto.CreateNewsSectionDto;
import com.register.wowlibre.domain.dto.NewsSectionsDto;
import com.register.wowlibre.domain.dto.NewsSummaryDto;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.port.out.news.DeleteNews;
import com.register.wowlibre.domain.port.out.news.ObtainNews;
import com.register.wowlibre.domain.port.out.news.SaveNews;
import com.register.wowlibre.domain.port.out.news_sections.DeleteNewsSections;
import com.register.wowlibre.domain.port.out.news_sections.ObtainNewsSections;
import com.register.wowlibre.domain.port.out.news_sections.SaveNewsSections;
import com.register.wowlibre.infrastructure.entities.NewsEntity;
import com.register.wowlibre.infrastructure.entities.NewsSectionsEntity;
import com.register.wowlibre.model.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsServiceTest extends BaseTest {

    @Mock
    private ObtainNews obtainNews;
    @Mock
    private SaveNews saveNews;
    @Mock
    private DeleteNews deleteNews;
    @Mock
    private ObtainNewsSections obtainNewsSections;
    @Mock
    private SaveNewsSections saveNewsSections;
    @Mock
    private DeleteNewsSections deleteNewsSections;

    private NewsService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new NewsService(obtainNews, saveNews, deleteNews, obtainNewsSections,
                saveNewsSections, deleteNewsSections);
    }

    @Test
    void news_shouldReturnListOfNewsSummaryDto() {
        int size = 10;
        int page = 0;
        String transactionId = "tx-news-001";
        NewsEntity newsEntity = createNewsEntity(1L, "Title", "SubTitle", "img.jpg", "Author");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<NewsEntity> pageResult = new PageImpl<>(List.of(newsEntity));

        when(obtainNews.findAllByOrderByUpdatedAtDesc(pageable)).thenReturn(pageResult);

        List<NewsSummaryDto> result = service.news(size, page, transactionId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id);
        assertEquals("Title", result.get(0).title);
        verify(obtainNews).findAllByOrderByUpdatedAtDesc(pageable);
    }

    @Test
    void newsSectionId_shouldReturnNewsSectionsDto() {
        Long newsId = 1L;
        String transactionId = "tx-news-002";
        NewsEntity newsEntity = createNewsEntity(newsId, "Title", "SubTitle", "img.jpg", "Author");
        NewsSectionsEntity section = createNewsSectionEntity(1L, newsEntity, "Section Title", "Content", 1);

        when(obtainNews.findById(newsId)).thenReturn(Optional.of(newsEntity));
        when(obtainNewsSections.findByNewsIdOrderBySectionOrderAsc(newsEntity)).thenReturn(List.of(section));

        NewsSectionsDto result = service.newsSectionId(newsId, transactionId);

        assertNotNull(result);
        assertEquals("Title", result.getTitle());
        assertEquals(1, result.getSections().size());
        verify(obtainNews).findById(newsId);
        verify(obtainNewsSections).findByNewsIdOrderBySectionOrderAsc(newsEntity);
    }

    @Test
    void newsSectionId_shouldThrowExceptionWhenNewsNotFound() {
        Long newsId = 999L;
        String transactionId = "tx-news-003";

        when(obtainNews.findById(newsId)).thenReturn(Optional.empty());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.newsSectionId(newsId, transactionId)
        );

        assertEquals("News not found with id: " + newsId, exception.getMessage());
        verify(obtainNews).findById(newsId);
        verifyNoInteractions(obtainNewsSections);
    }

    @Test
    void saveNews_shouldSaveNewsEntity() {
        String transactionId = "tx-news-004";
        CreateNewsDto createNewsDto = new CreateNewsDto();
        createNewsDto.title = "New Title";
        createNewsDto.subTitle = "New SubTitle";
        createNewsDto.imgUrl = "new-img.jpg";
        createNewsDto.author = "New Author";

        ArgumentCaptor<NewsEntity> captor = ArgumentCaptor.forClass(NewsEntity.class);
        service.saveNews(createNewsDto, transactionId);

        verify(saveNews).save(captor.capture());
        NewsEntity savedEntity = captor.getValue();
        assertEquals("New Title", savedEntity.getTitle());
        assertEquals("New SubTitle", savedEntity.getSubTitle());
        assertEquals("new-img.jpg", savedEntity.getImgUrl());
        assertEquals("New Author", savedEntity.getAuthor());
    }

    @Test
    void deleteNews_shouldDeleteNewsEntity() {
        Long newsId = 1L;
        String transactionId = "tx-news-005";
        NewsEntity newsEntity = createNewsEntity(newsId, "Title", "SubTitle", "img.jpg", "Author");

        when(obtainNews.findById(newsId)).thenReturn(Optional.of(newsEntity));

        service.deleteNews(newsId, transactionId);

        verify(obtainNews).findById(newsId);
        verify(deleteNews).delete(newsEntity);
    }

    @Test
    void deleteNews_shouldThrowExceptionWhenNewsNotFound() {
        Long newsId = 999L;
        String transactionId = "tx-news-006";

        when(obtainNews.findById(newsId)).thenReturn(Optional.empty());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.deleteNews(newsId, transactionId)
        );

        assertEquals("News not found with id: " + newsId, exception.getMessage());
        verify(obtainNews).findById(newsId);
        verifyNoInteractions(deleteNews);
    }

    @Test
    void updateNews_shouldUpdateNewsEntity() {
        Long newsId = 1L;
        String transactionId = "tx-news-007";
        NewsEntity newsEntity = createNewsEntity(newsId, "Old Title", "Old SubTitle", "old-img.jpg", "Old Author");
        UpdateNewsDto updateNewsDto = new UpdateNewsDto();
        updateNewsDto.title = "Updated Title";
        updateNewsDto.subTitle = "Updated SubTitle";
        updateNewsDto.imgUrl = "updated-img.jpg";
        updateNewsDto.author = "Updated Author";

        when(obtainNews.findById(newsId)).thenReturn(Optional.of(newsEntity));

        service.updateNews(newsId, updateNewsDto, transactionId);

        verify(obtainNews).findById(newsId);
        verify(saveNews).save(newsEntity);
        assertEquals("Updated Title", newsEntity.getTitle());
        assertEquals("Updated SubTitle", newsEntity.getSubTitle());
        assertEquals("updated-img.jpg", newsEntity.getImgUrl());
        assertEquals("Updated Author", newsEntity.getAuthor());
    }

    @Test
    void updateNews_shouldThrowExceptionWhenNewsNotFound() {
        Long newsId = 999L;
        String transactionId = "tx-news-008";
        UpdateNewsDto updateNewsDto = new UpdateNewsDto();

        when(obtainNews.findById(newsId)).thenReturn(Optional.empty());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.updateNews(newsId, updateNewsDto, transactionId)
        );

        assertEquals("News not found with id: " + newsId, exception.getMessage());
        verify(obtainNews).findById(newsId);
        verifyNoInteractions(saveNews);
    }

    @Test
    void createNewsSection_shouldCreateNewsSection() {
        Long newsId = 1L;
        String transactionId = "tx-news-009";
        NewsEntity newsEntity = createNewsEntity(newsId, "Title", "SubTitle", "img.jpg", "Author");
        CreateNewsSectionDto createNewsSectionDto = new CreateNewsSectionDto();
        createNewsSectionDto.title = "Section Title";
        createNewsSectionDto.content = "Section Content";
        createNewsSectionDto.imgUrl = "section-img.jpg";

        when(obtainNews.findById(newsId)).thenReturn(Optional.of(newsEntity));
        when(obtainNewsSections.countBySelectOrder(newsEntity)).thenReturn(0);

        ArgumentCaptor<NewsSectionsEntity> captor = ArgumentCaptor.forClass(NewsSectionsEntity.class);
        service.createNewsSection(newsId, createNewsSectionDto, transactionId);

        verify(obtainNews).findById(newsId);
        verify(obtainNewsSections).countBySelectOrder(newsEntity);
        verify(saveNewsSections).save(captor.capture());
        NewsSectionsEntity savedSection = captor.getValue();
        assertEquals("Section Title", savedSection.getTitle());
        assertEquals("Section Content", savedSection.getContent());
        assertEquals(0, savedSection.getSectionOrder());
    }

    @Test
    void createNewsSection_shouldThrowExceptionWhenNewsNotFound() {
        Long newsId = 999L;
        String transactionId = "tx-news-010";
        CreateNewsSectionDto createNewsSectionDto = new CreateNewsSectionDto();

        when(obtainNews.findById(newsId)).thenReturn(Optional.empty());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.createNewsSection(newsId, createNewsSectionDto, transactionId)
        );

        assertEquals("News not found with id: " + newsId, exception.getMessage());
        verify(obtainNews).findById(newsId);
        verifyNoInteractions(obtainNewsSections, saveNewsSections);
    }

    @Test
    void deleteNewsSection_shouldDeleteNewsSection() {
        Long newsId = 1L;
        Long sectionId = 1L;
        String transactionId = "tx-news-011";
        NewsEntity newsEntity = createNewsEntity(newsId, "Title", "SubTitle", "img.jpg", "Author");
        NewsSectionsEntity section = createNewsSectionEntity(sectionId, newsEntity, "Section Title", "Content", 1);

        when(obtainNews.findById(newsId)).thenReturn(Optional.of(newsEntity));
        when(obtainNewsSections.findByIdAndNewsEntity(sectionId, newsEntity)).thenReturn(Optional.of(section));

        service.deleteNewsSection(newsId, sectionId, transactionId);

        verify(obtainNews).findById(newsId);
        verify(obtainNewsSections).findByIdAndNewsEntity(sectionId, newsEntity);
        verify(deleteNewsSections).delete(section);
    }

    @Test
    void deleteNewsSection_shouldThrowExceptionWhenNewsNotFound() {
        Long newsId = 999L;
        Long sectionId = 1L;
        String transactionId = "tx-news-012";

        when(obtainNews.findById(newsId)).thenReturn(Optional.empty());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.deleteNewsSection(newsId, sectionId, transactionId)
        );

        assertEquals("News not found with id: " + newsId, exception.getMessage());
        verify(obtainNews).findById(newsId);
        verifyNoInteractions(obtainNewsSections, deleteNewsSections);
    }

    @Test
    void deleteNewsSection_shouldThrowExceptionWhenSectionNotFound() {
        Long newsId = 1L;
        Long sectionId = 999L;
        String transactionId = "tx-news-013";
        NewsEntity newsEntity = createNewsEntity(newsId, "Title", "SubTitle", "img.jpg", "Author");

        when(obtainNews.findById(newsId)).thenReturn(Optional.of(newsEntity));
        when(obtainNewsSections.findByIdAndNewsEntity(sectionId, newsEntity)).thenReturn(Optional.empty());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.deleteNewsSection(newsId, sectionId, transactionId)
        );

        assertEquals("News section not found with id: " + sectionId + " for news id: " + newsId, exception.getMessage());
        verify(obtainNews).findById(newsId);
        verify(obtainNewsSections).findByIdAndNewsEntity(sectionId, newsEntity);
        verifyNoInteractions(deleteNewsSections);
    }

    private NewsEntity createNewsEntity(Long id, String title, String subTitle, String imgUrl, String author) {
        NewsEntity entity = new NewsEntity();
        entity.setId(id);
        entity.setTitle(title);
        entity.setSubTitle(subTitle);
        entity.setImgUrl(imgUrl);
        entity.setAuthor(author);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    private NewsSectionsEntity createNewsSectionEntity(Long id, NewsEntity newsEntity, String title,
                                                        String content, Integer order) {
        NewsSectionsEntity entity = new NewsSectionsEntity();
        entity.setId(id);
        entity.setNewsId(newsEntity);
        entity.setTitle(title);
        entity.setContent(content);
        entity.setSectionOrder(order);
        return entity;
    }
}

