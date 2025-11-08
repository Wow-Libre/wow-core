package com.register.wowlibre.controller;

import com.register.wowlibre.application.services.realm_advertising.RealmAdvertisingService;
import com.register.wowlibre.domain.dto.RealmAdvertisingDto;
import com.register.wowlibre.domain.model.RealmAdvertisingModel;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.infrastructure.controller.RealmAdvertisingController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RealmAdvertisingControllerTest {

    @Mock
    private RealmAdvertisingService realmAdvertisingService;

    @InjectMocks
    private RealmAdvertisingController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnRealmAdvertisingByRealmId() {
        String transactionId = "tx-realm-adv-001";
        Long realmId = 1L;
        Locale locale = Locale.ENGLISH;
        RealmAdvertisingModel model = RealmAdvertisingModel.builder()
                .id(realmId)
                .title("Test Title")
                .tag("TEST")
                .subTitle("Test Subtitle")
                .description("Test Description")
                .ctaPrimary("Click Here")
                .imgUrl("https://example.com/image.jpg")
                .copySuccess(false)
                .redirect("https://example.com")
                .footerDisclaimer("Test Disclaimer")
                .realmlist("test.realmlist")
                .build();

        when(realmAdvertisingService.getRealmAdvertisingById(realmId, locale.getLanguage(), transactionId))
                .thenReturn(model);

        ResponseEntity<GenericResponse<RealmAdvertisingModel>> response = controller.getByRealmId(
                transactionId, locale, realmId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotNull();
        assertEquals(realmId, response.getBody().getData().id);
        verify(realmAdvertisingService).getRealmAdvertisingById(realmId, locale.getLanguage(), transactionId);
    }

    @Test
    void shouldSaveRealmAdvertising() {
        String transactionId = "tx-realm-adv-002";
        Long realmId = 1L;
        RealmAdvertisingDto dto = new RealmAdvertisingDto();
        dto.setTag("TEST");
        dto.setSubTitle("Test Subtitle");
        dto.setDescription("Test Description");
        dto.setCtaPrimary("Click Here");
        dto.setImgUrl("https://example.com/image.jpg");
        dto.setFooterDisclaimer("Test Disclaimer");
        dto.setLanguage("en");

        ResponseEntity<GenericResponse<Void>> response = controller.save(
                transactionId, dto, realmId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(realmAdvertisingService).save(dto, realmId, transactionId);
    }

    @Test
    void shouldReturnRealmAdvertisingByLanguage() {
        String transactionId = "tx-realm-adv-003";
        Locale locale = Locale.ENGLISH;
        List<RealmAdvertisingModel> models = new ArrayList<>();
        RealmAdvertisingModel model1 = RealmAdvertisingModel.builder()
                .id(1L)
                .title("Title 1")
                .tag("TAG1")
                .subTitle("Subtitle 1")
                .description("Description 1")
                .ctaPrimary("CTA 1")
                .imgUrl("img1.jpg")
                .copySuccess(false)
                .redirect("redirect1")
                .footerDisclaimer("Disclaimer 1")
                .realmlist("realmlist1")
                .build();
        RealmAdvertisingModel model2 = RealmAdvertisingModel.builder()
                .id(2L)
                .title("Title 2")
                .tag("TAG2")
                .subTitle("Subtitle 2")
                .description("Description 2")
                .ctaPrimary("CTA 2")
                .imgUrl("img2.jpg")
                .copySuccess(false)
                .redirect("redirect2")
                .footerDisclaimer("Disclaimer 2")
                .realmlist("realmlist2")
                .build();
        models.add(model1);
        models.add(model2);

        when(realmAdvertisingService.findByRealmsByLanguage(locale.getLanguage(), transactionId))
                .thenReturn(models);

        ResponseEntity<GenericResponse<List<RealmAdvertisingModel>>> response = controller.findByLanguage(
                transactionId, locale
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(2);
        verify(realmAdvertisingService).findByRealmsByLanguage(locale.getLanguage(), transactionId);
    }

    @Test
    void shouldReturnEmptyListWhenNoRealmAdvertisingByLanguage() {
        String transactionId = "tx-realm-adv-004";
        Locale locale = Locale.ENGLISH;
        List<RealmAdvertisingModel> models = new ArrayList<>();

        when(realmAdvertisingService.findByRealmsByLanguage(locale.getLanguage(), transactionId))
                .thenReturn(models);

        ResponseEntity<GenericResponse<List<RealmAdvertisingModel>>> response = controller.findByLanguage(
                transactionId, locale
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEmpty();
        verify(realmAdvertisingService).findByRealmsByLanguage(locale.getLanguage(), transactionId);
    }
}

