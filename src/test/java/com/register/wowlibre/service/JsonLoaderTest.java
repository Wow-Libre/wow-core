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

        Map<String, List<WidgetHomeSubscriptionModel>> widgets = Map.of("en",
                List.of(new WidgetHomeSubscriptionModel()));

        // Mock streams
        when(jsonFile.getInputStream()).thenReturn(dummyStream());
        when(bankPlans.getInputStream()).thenReturn(dummyStream());
        when(benefitsGuild.getInputStream()).thenReturn(dummyStream());
        when(widgetHomeSubscription.getInputStream()).thenReturn(dummyStream());
        when(plansAcquisition.getInputStream()).thenReturn(dummyStream());

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(countries)
                .thenReturn(plans)
                .thenReturn(widgets);

        jsonLoader = new JsonLoader(objectMapper, jsonFile, bankPlans, benefitsGuild, widgetHomeSubscription);

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
        assertEquals("us", result.getFirst().value());
    }

    @Test
    void testGetJsonPlans() {
        List<PlanModel> result = jsonLoader.getJsonPlans("en", "tx123");
        assertEquals(1, result.size());
        assertEquals("Plan1", result.getFirst().name());
    }


}
