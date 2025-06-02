package com.register.wowlibre.domain.port.in.news;

import com.register.wowlibre.domain.*;
import com.register.wowlibre.domain.dto.*;

import java.util.*;

public interface NewsPort {
    List<NewsSummaryDto> news(int size, int page, String transactionId);

    NewsSectionsDto newsSectionId(Long newsId, String transactionId);

    void saveNews(CreateNewsDto createNewsDto, String transactionId);

    void deleteNews(Long newsId, String transactionId);

    void updateNews(Long newsId, UpdateNewsDto updateNewsDto, String transactionId);

    void createNewsSection(Long newsId, CreateNewsSectionDto createNewsSectionDto, String transactionId);

    void deleteNewsSection(Long newsId, Long sectionId, String transactionId);
}
