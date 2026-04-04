package com.register.wowlibre.application.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class ChdmpPayloadParserTest {

    @Test
    void extractJsonString_fromBase64_returnsDecodedJson() {
        String json = "{\"uinf\":{\"name\":\"Foo\",\"guid\":\"Player-1\"}}";
        String b64 = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        String out = ChdmpPayloadParser.extractJsonString(b64);
        assertTrue(out.contains("Foo"));
        assertTrue(out.contains("Player-1"));
    }

    /** Como {@code WoWArgMigrador.lua (1).bak}: JSON dentro de Lua con {@code \"}. */
    @Test
    void extractJsonString_fromLuaChdmpData_returnsInnerJson() {
        String inner = "{\"a\":1}";
        String lua = "CHDMP_DATA = \"" + inner.replace("\"", "\\\"") + "\"\nCHDMP_KEY = \"x\"";
        String out = ChdmpPayloadParser.extractJsonString(lua);
        assertEquals(inner, out);
    }

    /** Como {@code WoWArgMigrador.lua}: líneas en blanco al inicio + CHDMP_DATA con Base64 puro (sin escapes). */
    @Test
    void extractJsonString_luaLeadingBlankLines_base64LikeProductionFile() {
        String json = "{\"uinf\":{\"name\":\"ProdStyle\"}}";
        String b64 = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        String lua = "\n\nCHDMP_DATA = \"" + b64 + "\"\nCHDMP_KEY = \"x\"\n";
        String out = ChdmpPayloadParser.extractJsonString(lua);
        assertTrue(out.contains("ProdStyle"));
    }

    /** Mismo formato Base64 en CHDMP_DATA que el addon en uso normal. */
    @Test
    void extractJsonString_chdmpData_holdsBase64OfJson_decodesLikeAddon() {
        String json = "{\"uinf\":{\"name\":\"FromB64\",\"guid\":\"G-1\"}}";
        String b64 = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        String lua = "CHDMP_DATA = \"" + b64 + "\"\nCHDMP_KEY = \"x\"";
        String out = ChdmpPayloadParser.extractJsonString(lua);
        assertTrue(out.contains("FromB64"));
        assertTrue(out.contains("uinf"));
    }

    @Test
    void extractJsonString_plainJsonFile_noBase64() {
        String json = "{\"x\":[]}";
        assertEquals(json, ChdmpPayloadParser.extractJsonString("  " + json + "  "));
    }

    @Test
    void extractJsonString_invalidBase64_throws() {
        assertThrows(IllegalArgumentException.class, () -> ChdmpPayloadParser.extractJsonString("not-base64!!!"));
    }

    /**
     * Algunos clientes serializan JSON en UTF-16LE; Base64 de esos bytes mal leídos como UTF-8 dan error
     * tipo "Unrecognized token 'ϕ'" en columna 2.
     */
    @Test
    void extractJsonString_base64OfUtf16LeJson_recovers() {
        String json = "{\"uinf\":{\"name\":\"Wide\"}}";
        byte[] utf16le = json.getBytes(StandardCharsets.UTF_16LE);
        String b64 = Base64.getEncoder().encodeToString(utf16le);
        String out = ChdmpPayloadParser.extractJsonString(b64);
        assertTrue(out.contains("Wide"));
        assertTrue(out.contains("uinf"));
    }

    @Test
    void extractJsonString_lua_chdmpData_base64Utf16Le() {
        String json = "{\"a\":1}";
        String b64 = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_16LE));
        String lua = "CHDMP_DATA = \"" + b64 + "\"\n";
        String out = ChdmpPayloadParser.extractJsonString(lua);
        assertTrue(out.contains("\"a\":1"));
    }

    /**
     * Formato real del addon: {@code b64.lua} (reverse + Base64 + reverse) aplicado dos veces en
     * {@code GetCharDump()}. Cadena externa precomputada para {@code {"a":1}}.
     */
    @Test
    void extractJsonString_wowArgMigrador_doubleCustomB64() {
        String outer = "wdllWRtlkNFRlZ";
        String lua = "CHDMP_DATA = \"" + outer + "\"\nCHDMP_KEY = \"f7519722aa975a5dab2e49c18d9b175cd8047a36\"\n";
        String out = ChdmpPayloadParser.extractJsonString(lua);
        assertTrue(out.contains("\"a\":1"));
    }
}
