package com.register.wowlibre.application.services.news;

import com.register.wowlibre.domain.*;
import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.port.in.news.*;
import com.register.wowlibre.domain.port.out.news.*;
import com.register.wowlibre.domain.port.out.news_sections.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class NewsService implements NewsPort {
    private final ObtainNews obtainNews;
    private final SaveNews saveNews;
    private final DeleteNews deleteNews;
    private final ObtainNewsSections obtainNewsSections;
    private final SaveNewsSections saveNewsSections;
    private final DeleteNewsSections deleteNewsSections;

    public NewsService(ObtainNews obtainNews, SaveNews saveNews, DeleteNews deleteNews,
                       ObtainNewsSections obtainNewsSections, SaveNewsSections saveNewsSections,
                       DeleteNewsSections deleteNewsSections) {
        this.obtainNews = obtainNews;
        this.saveNews = saveNews;
        this.deleteNews = deleteNews;
        this.obtainNewsSections = obtainNewsSections;
        this.saveNewsSections = saveNewsSections;
        this.deleteNewsSections = deleteNewsSections;
    }


    @Override
    public List<NewsSummaryDto> news(int size, int page, String transactionId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        return obtainNews.findAllByOrderByUpdatedAtDesc(pageable)
                .stream()
                .map(newsEntity -> {
                    NewsSummaryDto newsSummaryDto = new NewsSummaryDto();
                    newsSummaryDto.id = newsEntity.getId();
                    newsSummaryDto.title = newsEntity.getTitle();
                    newsSummaryDto.subTitle = newsEntity.getSubTitle();
                    newsSummaryDto.imgUrl = newsEntity.getImgUrl();
                    newsSummaryDto.createdAt = newsEntity.getCreatedAt();
                    newsSummaryDto.author = newsEntity.getAuthor();
                    return newsSummaryDto;
                })
                .toList();
    }

    @Override
    public NewsSectionsDto newsSectionId(Long newsId, String transactionId) {

        Optional<NewsEntity> news = obtainNews.findById(newsId);

        if (news.isEmpty()) {
            throw new InternalException("News not found with id: " + newsId, transactionId);
        }

        final NewsEntity newsModel = news.get();

        NewsSectionsDto data = new NewsSectionsDto();
        data.setAuthor(newsModel.getAuthor());
        data.setCreatedAt(newsModel.getCreatedAt());
        data.setImgUrl(newsModel.getImgUrl());
        data.setSubTitle(newsModel.getSubTitle());
        data.setTitle(newsModel.getTitle());
        data.setSections(obtainNewsSections.findByNewsIdOrderBySectionOrderAsc(news.get()).stream().map(
                newsSectionsEntity ->
                        new NewsSectionsDto.Section(
                                newsSectionsEntity.getId(),
                                newsSectionsEntity.getTitle(),
                                newsSectionsEntity.getContent(),
                                newsSectionsEntity.getImgUrl(),
                                newsSectionsEntity.getSectionOrder())).toList());
        return data;
    }

    @Override
    public void saveNews(CreateNewsDto createNewsDto, String transactionId) {
        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setTitle(createNewsDto.title);
        newsEntity.setSubTitle(createNewsDto.subTitle);
        newsEntity.setImgUrl(createNewsDto.imgUrl);
        newsEntity.setAuthor(createNewsDto.author);
        saveNews.save(newsEntity);
    }

    @Override
    public void deleteNews(Long newsId, String transactionId) {
        Optional<NewsEntity> news = obtainNews.findById(newsId);

        if (news.isEmpty()) {
            throw new InternalException("News not found with id: " + newsId, transactionId);
        }

        deleteNews.delete(news.get());
    }

    @Override
    public void updateNews(Long newsId, UpdateNewsDto updateNewsDto, String transactionId) {
        Optional<NewsEntity> news = obtainNews.findById(newsId);

        if (news.isEmpty()) {
            throw new InternalException("News not found with id: " + newsId, transactionId);
        }
        NewsEntity newsEntity = news.get();
        newsEntity.setTitle(updateNewsDto.title);
        newsEntity.setSubTitle(updateNewsDto.subTitle);
        newsEntity.setImgUrl(updateNewsDto.imgUrl);
        newsEntity.setAuthor(updateNewsDto.author);
        saveNews.save(newsEntity);

    }

    @Override
    public void createNewsSection(Long newsId, CreateNewsSectionDto createNewsSectionDto, String transactionId) {
        Optional<NewsEntity> news = obtainNews.findById(newsId);

        if (news.isEmpty()) {
            throw new InternalException("News not found with id: " + newsId, transactionId);
        }
        NewsEntity newsEntity = news.get();

        Integer order = Optional.ofNullable(obtainNewsSections.countBySelectOrder(newsEntity)).orElse(0);


        NewsSectionsEntity newsSectionsEntity = new NewsSectionsEntity();
        newsSectionsEntity.setNewsId(newsEntity);
        newsSectionsEntity.setTitle(createNewsSectionDto.title);
        newsSectionsEntity.setContent(createNewsSectionDto.content);
        newsSectionsEntity.setImgUrl(createNewsSectionDto.imgUrl);
        newsSectionsEntity.setSectionOrder(order);

        saveNewsSections.save(newsSectionsEntity);
    }

    @Override
    public void deleteNewsSection(Long newsId, Long sectionId, String transactionId) {
        Optional<NewsEntity> news = obtainNews.findById(newsId);

        if (news.isEmpty()) {
            throw new InternalException("News not found with id: " + newsId, transactionId);
        }


        Optional<NewsSectionsEntity> sectionNews = obtainNewsSections.findByIdAndNewsEntity(sectionId, news.get());

        if (sectionNews.isEmpty()) {
            throw new InternalException("News section not found with id: " + sectionId + " for news id: " + newsId,
                    transactionId);
        }

        deleteNewsSections.delete(sectionNews.get());
    }
}
