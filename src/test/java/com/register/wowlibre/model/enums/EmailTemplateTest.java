package com.register.wowlibre.model.enums;

import com.register.wowlibre.domain.enums.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class EmailTemplateTest {
    @Test
    void testGetTemplateNameByValidId() {
        assertEquals("register.ftlh", EmailTemplate.getTemplateNameById(1));
    }

    @Test
    void testGetTemplateNameByInvalidIdReturnsDefault() {
        assertEquals("register.ftlh", EmailTemplate.getTemplateNameById(999));
        assertEquals("register.ftlh", EmailTemplate.getTemplateNameById(-1));
        assertEquals("register.ftlh", EmailTemplate.getTemplateNameById(0));
    }
}
