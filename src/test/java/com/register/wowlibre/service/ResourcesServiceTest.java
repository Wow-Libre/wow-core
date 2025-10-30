package com.register.wowlibre.service;

import com.register.wowlibre.application.services.resources.*;
import com.register.wowlibre.domain.dto.faqs.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.resources.*;
import com.register.wowlibre.domain.port.out.*;
import com.register.wowlibre.domain.port.out.faqs.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResourcesServiceTest {
    private JsonLoaderPort jsonLoaderPort;
    private ObtainFaqs obtainFaqs;
    private SaveFaqs saveFaqs;
    private ResourcesService resourcesService;

    @BeforeEach
    void setUp() {
        jsonLoaderPort = mock(JsonLoaderPort.class);
        obtainFaqs = mock(ObtainFaqs.class);
        saveFaqs = mock(SaveFaqs.class);
        resourcesService = new ResourcesService(jsonLoaderPort, obtainFaqs, saveFaqs);
    }

    @Test
    void testGetCountry() {
        List<CountryModel> mockCountries = List.of(new CountryModel("US", "United States", "en", "us-site"));
        when(jsonLoaderPort.getJsonCountry("tx123")).thenReturn(mockCountries);

        List<CountryModel> result = resourcesService.getCountry("tx123");

        assertEquals(1, result.size());
        assertEquals("US", result.get(0).value());
    }

    @Test
    void testGetFaqs() {
        FaqsEntity faqsEntity = new FaqsEntity();
        faqsEntity.setId(1L);
        faqsEntity.setQuestion("Q?");
        faqsEntity.setAnswer("A!");
        faqsEntity.setType(FaqType.SUPPORT);
        faqsEntity.setLanguage("en");

        when(obtainFaqs.findByTypeAndLanguage(FaqType.SUPPORT, "en")).thenReturn(List.of(faqsEntity));

        List<FaqsModel> result = resourcesService.getFaqs(FaqType.SUPPORT, "en", "tx123");

        assertEquals(1, result.size());
        assertEquals("Q?", result.get(0).question());
        assertEquals(FaqType.SUPPORT, result.get(0).type());
    }

    @Test
    void testCreateFaqWithValidType() {
        CreateFaqDto dto = new CreateFaqDto();
        dto.setAnswer("A!");
        dto.setQuestion("Q?");
        dto.setType(FaqType.SUPPORT.name());
        dto.setLanguage("en");
        resourcesService.createFaq(dto, "tx123");

        verify(saveFaqs, times(1)).save(any(FaqsEntity.class), eq("tx123"));
    }

    @Test
    void testCreateFaqWithInvalidTypeThrowsException() {
        CreateFaqDto dto = new CreateFaqDto();
        dto.setAnswer("A!");
        dto.setQuestion("Q?");
        dto.setType("invalid_type");

        InternalException ex = assertThrows(InternalException.class, () -> resourcesService.createFaq(dto, "tx123"));

        assertEquals("Invalid FAQ type: ", ex.getMessage());
        assertEquals("tx123", ex.transactionId);
    }

    @Test
    void testDeleteFaq() {
        resourcesService.deleteFaq(1L, "tx123");

        verify(saveFaqs).delete(1L, "tx123");
    }

    @Test
    void testGetPlansBank() {
        List<PlanModel> plans = List.of(new PlanModel(1L, "Plan A", "desc", "10", "monthly", List.of("feature1"),
                "Buy", 5, 1, 99.9));
        when(jsonLoaderPort.getJsonPlans("en", "tx123")).thenReturn(plans);

        List<PlanModel> result = resourcesService.getPlansBank("en", "tx123");

        assertEquals(1, result.size());
        assertEquals("Plan A", result.get(0).name());
    }

    @Test
    void testGetBenefitsGuild() {
        List<BenefitModel> benefits = List.of(new BenefitModel(1L, "Title", "Sub", "Desc", "logo.png", "itemId", 2,
                true, "link.com"));
        when(jsonLoaderPort.getJsonBenefitsGuild("en", "tx123")).thenReturn(benefits);

        List<BenefitModel> result = resourcesService.getBenefitsGuild("en", "tx123");

        assertEquals(1, result.size());
        assertTrue(result.get(0).status);
    }

    @Test
    void testGetWidgetSubscription() {
        WidgetHomeSubscriptionModel widget = new WidgetHomeSubscriptionModel();
        widget.title = "Title";
        widget.description = "Desc";
        widget.btn = "cta";

        when(jsonLoaderPort.getWidgetSubscription("en", "tx123")).thenReturn(widget);

        WidgetHomeSubscriptionModel result = resourcesService.getWidgetSubscription("en", "tx123");

        assertNotNull(result);
        assertEquals("Title", result.title);
        assertEquals("Desc", result.description);
        assertEquals("cta", result.btn);
    }

    @Test
    void testGetPlansAcquisition() {
        List<PlanAcquisitionModel> acquisitions = List.of(new PlanAcquisitionModel("Basic", "10.99", "Info", List.of(
                "Feature1"), "Buy now", "https://link.com"));
        when(jsonLoaderPort.getPlansAcquisition("en", "tx123")).thenReturn(acquisitions);

        List<PlanAcquisitionModel> result = resourcesService.getPlansAcquisition("en", "tx123");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Basic", result.get(0).name());
        assertEquals("10.99", result.get(0).price());
        assertEquals("Info", result.get(0).description());
        assertEquals(List.of("Feature1"), result.get(0).features());
        assertEquals("Buy now", result.get(0).buttonText());
        assertEquals("https://link.com", result.get(0).url());
    }
}
