package com.register.wowlibre.repository;

import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.repositories.faqs.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaFaqsAdapterTest {
    @Mock
    private FaqsRepository faqsRepository;

    private JpaFaqsAdapter jpaFaqsAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jpaFaqsAdapter = new JpaFaqsAdapter(faqsRepository);
    }

    @Test
    void testFindByLanguage_returnsFaqList() {
        // Arrange
        String language = "es";
        List<FaqsEntity> expectedFaqs = List.of(
                createFaqEntity(1L, "¿Qué es?", "Una prueba.", "en"),
                createFaqEntity(2L, "¿Cómo funciona?", "Con magia.", "es")
        );

        when(faqsRepository.findByTypeAndLanguage(FaqType.SUPPORT, language)).thenReturn(expectedFaqs);

        // Act
        List<FaqsEntity> actualFaqs = jpaFaqsAdapter.findByTypeAndLanguage(FaqType.SUPPORT, language);

        // Assert
        assertEquals(expectedFaqs.size(), actualFaqs.size());
        assertEquals(expectedFaqs, actualFaqs);
        verify(faqsRepository, times(1)).findByTypeAndLanguage(FaqType.SUPPORT, language);
    }

    @Test
    void testSaveFaq() {
        FaqsEntity entity = new FaqsEntity();
        String txId = "tx123";

        jpaFaqsAdapter.save(entity, txId);

        verify(faqsRepository, times(1)).save(entity);
    }

    @Test
    void testDeleteExistingFaq() {
        Long faqId = 1L;
        FaqsEntity entity = new FaqsEntity();

        when(faqsRepository.findById(faqId)).thenReturn(Optional.of(entity));

        jpaFaqsAdapter.delete(faqId, "tx123");

        verify(faqsRepository, times(1)).findById(faqId);
        verify(faqsRepository, times(1)).delete(entity);
    }

    @Test
    void testDeleteNonExistingFaq() {
        Long faqId = 99L;

        when(faqsRepository.findById(faqId)).thenReturn(Optional.empty());

        jpaFaqsAdapter.delete(faqId, "tx456");

        verify(faqsRepository, times(1)).findById(faqId);
        verify(faqsRepository, never()).delete(any());
    }

    private FaqsEntity createFaqEntity(Long id, String question, String answer, String language) {
        FaqsEntity faq = new FaqsEntity();
        faq.setId(id);
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faq.setLanguage(language);
        return faq;
    }
}
