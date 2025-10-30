package com.register.wowlibre.service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import com.register.wowlibre.domain.model.resources.*;
import com.register.wowlibre.infrastructure.repositories.*;
import org.junit.jupiter.api.*;
import org.springframework.core.io.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JsonLoaderTest {

    private JsonLoader jsonLoader;

    @BeforeEach
    void setUp() throws Exception {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        Resource jsonFile = mock(Resource.class);
        Resource bankPlans = mock(Resource.class);
        Resource benefitsGuild = mock(Resource.class);
        Resource widgetHomeSubscription = mock(Resource.class);
        Resource plansAcquisition = mock(Resource.class);

        // Simular datos
        List<CountryModel> countries = List.of(new CountryModel("us", "USA", "en", "wow.com"));
        Map<String, List<PlanModel>> plans = Map.of("en", List.of(new PlanModel(1L, "Plan1", "desc", "10", "monthly",
                List.of(), "Buy", 0, 1, 100.0)));
        Map<String, List<BenefitModel>> benefits = Map.of("en", List.of(new BenefitModel(1L, "Title", "Sub", "desc",
                "logo", "item", 1, true, "link")));
        Map<String, List<WidgetHomeSubscriptionModel>> widgets = Map.of("en",
                List.of(new WidgetHomeSubscriptionModel()));
        Map<String, List<PlanAcquisitionModel>> acquisitions = Map.of("en", List.of(new PlanAcquisitionModel("Plan",
                "9.99", "Desc", List.of(), "Buy", "url")));

        // Mock streams
        when(jsonFile.getInputStream()).thenReturn(dummyStream());
        when(bankPlans.getInputStream()).thenReturn(dummyStream());
        when(benefitsGuild.getInputStream()).thenReturn(dummyStream());
        when(widgetHomeSubscription.getInputStream()).thenReturn(dummyStream());
        when(plansAcquisition.getInputStream()).thenReturn(dummyStream());

        // Mock deserialización en orden correcto
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(countries) // jsonCountryModel
                .thenReturn(plans) // jsonPlanModel
                .thenReturn(benefits) // jsonBenefits
                .thenReturn(widgets) // jsonWidgetSubscription
                .thenReturn(acquisitions); // jsonPlanAcquisitionModel

        // Instanciar y cargar los datos con reflexión
        jsonLoader = new JsonLoader(objectMapper, jsonFile, bankPlans, benefitsGuild, widgetHomeSubscription,
                plansAcquisition);

        Method loadJsonFile = JsonLoader.class.getDeclaredMethod("loadJsonFile");
        loadJsonFile.setAccessible(true);
        loadJsonFile.invoke(jsonLoader);
    }

    private InputStream dummyStream() {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Test
    void testGetJsonCountry() {
        List<CountryModel> result = jsonLoader.getJsonCountry("tx123");
        assertEquals(1, result.size());
        assertEquals("us", result.get(0).value());
    }

    @Test
    void testGetJsonPlans() {
        List<PlanModel> result = jsonLoader.getJsonPlans("en", "tx123");
        assertEquals(1, result.size());
        assertEquals("Plan1", result.get(0).name());
    }

    @Test
    void testGetJsonBenefitsGuild() {
        List<BenefitModel> result = jsonLoader.getJsonBenefitsGuild("en", "tx123");
        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).title);
    }

    @Test
    void testGetPlansAcquisition() {
        List<PlanAcquisitionModel> result = jsonLoader.getPlansAcquisition("en", "tx123");
        assertEquals(1, result.size());
        assertEquals("Plan", result.get(0).name());
    }
}
