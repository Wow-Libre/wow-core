package com.register.wowlibre.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.banners.*;
import com.register.wowlibre.domain.shared.*;
import com.register.wowlibre.infrastructure.controller.*;
import org.junit.jupiter.api.*;
import org.springframework.http.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BannersControllerTest {

    private BannersPort bannersPort;
    private BannersController controller;

    @BeforeEach
    void setUp() {
        bannersPort = mock(BannersPort.class);
        controller = new BannersController(bannersPort);
    }

    @Test
    void banners_returnsOkWithList() {
        BannerModel banner = new BannerModel(1L, "", "", "", "", "");
        when(bannersPort.findByLanguage("es")).thenReturn(List.of(banner));

        ResponseEntity<GenericResponse<List<BannerModel>>> response = controller.banners("tx123", new Locale("es"));

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
        verify(bannersPort).findByLanguage("es");
    }

    @Test
    void saveBanner_callsPortAndReturnsCreated() {
        BannerDto dto = new BannerDto();

        ResponseEntity<GenericResponse<Void>> response = controller.saveBanner("tx123", dto);

        assertEquals(201, response.getStatusCode().value());
        verify(bannersPort).saveBanner(dto, "tx123");
    }

    @Test
    void deleteBanner_callsPortAndReturnsOk() {
        ResponseEntity<GenericResponse<Void>> response = controller.deleteBanner("tx123", 5L);

        assertEquals(200, response.getStatusCode().value());
        verify(bannersPort).deleteBanner(5L, "tx123");
    }
}
