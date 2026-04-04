package com.register.wowlibre.application.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * Extrae el texto JSON del dump del addon WoWArgMigrador / CHDMP.
 * <p>
 * Formatos reales que conviven:
 * </p>
 * <ul>
 *   <li><b>Producción ({@code WoWArgMigrador.lua})</b>: {@code CHDMP_DATA = "z5WSwVTb..."} — no es Base64 RFC
 *       directo del JSON: el addon usa {@code b64.lua}, que <b>revierte</b> la cadena, codifica a Base64 y
 *       <b>revierte</b> el resultado; además {@code GetCharDump()} hace {@code b64_enc(b64_enc(json))} (dos capas).
 *       Eso se invierte con {@link #decodeWowMigradorB64(String)} hasta dos veces si el Base64 estándar no da JSON.</li>
 *   <li><b>Sin Base64 ({@code *.bak}, export de prueba o versión alternativa)</b>:
 *       {@code CHDMP_DATA = "{\"uinf\":{...}}"} — JSON con comillas escapadas para Lua ({@code \"}).
 *       Tras extraer y des-escapar, el contenido <b>ya es JSON</b>; no se decodifica Base64.</li>
 *   <li><b>Solo Base64 en archivo</b> (sin línea Lua): todo el .txt es Base64 del JSON.</li>
 *   <li><b>JSON plano</b> (empieza por {@code '{' } o {@code '['}): se usa tal cual.</li>
 * </ul>
 */
public final class ChdmpPayloadParser {

    private ChdmpPayloadParser() {}

    public static String extractJsonString(String fileContent) {
        if (fileContent == null || fileContent.isBlank()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        String trimmed = fileContent.stripLeading();
        if (trimmed.startsWith("CHDMP_DATA")) {
            String fromLua = extractLuaChdmpDataJson(fileContent);
            if (fromLua == null || fromLua.isBlank()) {
                throw new IllegalArgumentException("No se encontró CHDMP_DATA en el archivo Lua");
            }
            return payloadToJsonUtf8(fromLua);
        }
        String whole = fileContent.strip();
        if (looksLikeJson(whole)) {
            return whole;
        }
        return decodeBase64ToUtf8(fileContent);
    }

    /** Contenido interno de CHDMP_DATA o un .txt que sea solo Base64. */
    static String payloadToJsonUtf8(String payload) {
        String t = payload.strip();
        if (looksLikeJson(t)) {
            return t;
        }
        return decodeBase64ToUtf8(payload);
    }

    private static boolean looksLikeJson(String s) {
        if (s.isEmpty()) {
            return false;
        }
        char c = s.charAt(0);
        return c == '{' || c == '[';
    }

    private static String decodeBase64ToUtf8(String fileOrBlob) {
        String b64 = fileOrBlob.replaceAll("\\s+", "");
        byte[] decoded;
        try {
            decoded = Base64.getMimeDecoder().decode(b64);
        } catch (IllegalArgumentException e) {
            try {
                decoded = Base64.getUrlDecoder().decode(b64);
            } catch (IllegalArgumentException e2) {
                throw new IllegalArgumentException(
                        "El contenido no es JSON ni Base64 válido (ni archivo Lua con CHDMP_DATA)", e);
            }
        }
        decoded = tryDecompressIfCompressed(decoded);
        String text = bytesToJsonText(decoded);
        if (!looksLikeJson(text)) {
            String fromWow = tryDecodeWowMigradorDoubleB64(b64);
            if (fromWow != null) {
                return fromWow;
            }
        }
        if (!looksLikeJson(text) && looksLikeAsciiBase64Only(text)) {
            try {
                byte[] inner = Base64.getMimeDecoder().decode(text.replaceAll("\\s+", ""));
                inner = tryDecompressIfCompressed(inner);
                String innerText = bytesToJsonText(inner);
                if (looksLikeJson(innerText)) {
                    return innerText;
                }
            } catch (IllegalArgumentException ignored) {
                // una sola capa Base64
            }
        }
        return text;
    }

    /**
     * Inverso de {@code b64_enc} en {@code b64.lua} del addon WoWArgMigrador: revierte el Base64, decodifica,
     * revierte los bytes del payload (Lua trabaja por byte).
     */
    static String decodeWowMigradorB64(String encodedAscii) {
        String compact = encodedAscii.replaceAll("\\s+", "");
        String mid = new StringBuilder(compact).reverse().toString();
        byte[] decoded = Base64.getMimeDecoder().decode(mid);
        reverseInPlace(decoded);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    private static void reverseInPlace(byte[] arr) {
        for (int i = 0, j = arr.length - 1; i < j; i++, j--) {
            byte t = arr[i];
            arr[i] = arr[j];
            arr[j] = t;
        }
    }

    /**
     * {@code GetCharDump()} = {@code b64_enc(CreateCharDump())} y CreateCharDump ya devuelve {@code b64_enc(json)}.
     */
    private static String tryDecodeWowMigradorDoubleB64(String outerB64) {
        try {
            String once = decodeWowMigradorB64(outerB64);
            if (looksLikeJson(once.strip())) {
                return once.strip();
            }
            String twice = decodeWowMigradorB64(once.replaceAll("\\s+", ""));
            if (looksLikeJson(twice.strip())) {
                return twice.strip();
            }
        } catch (IllegalArgumentException ignored) {
            // no es el formato del addon
        }
        return null;
    }

    /**
     * Tras Base64, algunos dumps vienen zlib/deflate o gzip antes del JSON; si se leen como UTF-8 salen
     * caracteres raros (p. ej. griegos) y Jackson falla en la columna 1–2.
     */
    private static byte[] tryDecompressIfCompressed(byte[] raw) {
        if (raw == null || raw.length < 4) {
            return raw;
        }
        if (raw[0] == (byte) 0x1f && raw[1] == (byte) 0x8b) {
            try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(raw))) {
                return readAll(gis);
            } catch (IOException ignored) {
                return raw;
            }
        }
        if (raw[0] == (byte) 0x78) {
            try (InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(raw))) {
                return readAll(iis);
            } catch (IOException ignored) {
                return raw;
            }
        }
        return raw;
    }

    private static byte[] readAll(java.io.InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        in.transferTo(out);
        return out.toByteArray();
    }

    private static String bytesToJsonText(byte[] data) {
        if (data.length >= 2 && data[0] == (byte) 0xFF && data[1] == (byte) 0xFE) {
            return stripBom(new String(data, StandardCharsets.UTF_16LE).strip());
        }
        if (data.length >= 2 && data[0] == (byte) 0xFE && data[1] == (byte) 0xFF) {
            return stripBom(new String(data, StandardCharsets.UTF_16BE).strip());
        }
        String utf8 = stripBom(new String(data, StandardCharsets.UTF_8).strip());
        /*
         * JSON en UTF-16LE sin BOM: {"  -> bytes 7B 00 22 00 ...
         * Leído como UTF-8 suele dar "{" y luego un carácter basura (NUL, letras griegas, etc.) → Jackson
         * falla en columna 2 ("Unrecognized token 'ϕ'" u otro).
         */
        if (utf8LooksLikeBrokenJsonStart(utf8)) {
            String le = stripBom(new String(data, StandardCharsets.UTF_16LE).strip());
            if (!utf8LooksLikeBrokenJsonStart(le) && looksLikeJson(le)) {
                return le;
            }
            String be = stripBom(new String(data, StandardCharsets.UTF_16BE).strip());
            if (!utf8LooksLikeBrokenJsonStart(be) && looksLikeJson(be)) {
                return be;
            }
        }
        return utf8;
    }

    /**
     * Tras UTF-8, ¿el primer token parece objeto/array JSON pero el siguiente carácter útil no encaja?
     */
    private static boolean utf8LooksLikeBrokenJsonStart(String s) {
        if (s.isEmpty()) {
            return false;
        }
        char f = s.charAt(0);
        if (f == '{') {
            int i = 1;
            while (i < s.length() && isJsonWhitespace(s.charAt(i))) {
                i++;
            }
            if (i >= s.length()) {
                return false;
            }
            char c = s.charAt(i);
            return c != '"' && c != '}';
        }
        if (f == '[') {
            int i = 1;
            while (i < s.length() && isJsonWhitespace(s.charAt(i))) {
                i++;
            }
            if (i >= s.length()) {
                return false;
            }
            char c = s.charAt(i);
            return c != '"'
                    && c != '{'
                    && c != '['
                    && c != ']'
                    && c != 't'
                    && c != 'f'
                    && c != 'n'
                    && !(c >= '0' && c <= '9')
                    && c != '-'
                    && c != ' ';
        }
        return false;
    }

    private static boolean isJsonWhitespace(char c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }

    private static String stripBom(String s) {
        if (s.isEmpty()) {
            return s;
        }
        if (s.charAt(0) == '\uFEFF') {
            return s.substring(1).strip();
        }
        return s;
    }

    /** Evita un segundo Base64 sobre basura UTF-8 (p. ej. bytes zlib mal interpretados). */
    private static boolean looksLikeAsciiBase64Only(String s) {
        String t = s.replaceAll("\\s+", "");
        if (t.length() < 8) {
            return false;
        }
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            boolean ok = (c >= 'A' && c <= 'Z')
                    || (c >= 'a' && c <= 'z')
                    || (c >= '0' && c <= '9')
                    || c == '+'
                    || c == '/'
                    || c == '=';
            if (!ok) {
                return false;
            }
        }
        return true;
    }

    static String extractLuaChdmpDataJson(String content) {
        int marker = content.indexOf("CHDMP_DATA");
        if (marker < 0) {
            return null;
        }
        int eq = content.indexOf('=', marker);
        if (eq < 0) {
            return null;
        }
        int startQuote = content.indexOf('"', eq);
        if (startQuote < 0) {
            return null;
        }
        return parseLuaQuotedString(content, startQuote + 1);
    }

    private static String parseLuaQuotedString(String s, int start) {
        StringBuilder sb = new StringBuilder();
        int i = start;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '"') {
                break;
            }
            if (c == '\\' && i + 1 < s.length()) {
                char e = s.charAt(i + 1);
                switch (e) {
                    case '"':
                        sb.append('"');
                        i += 2;
                        continue;
                    case '\\':
                        sb.append('\\');
                        i += 2;
                        continue;
                    case 'n':
                        sb.append('\n');
                        i += 2;
                        continue;
                    case 'r':
                        sb.append('\r');
                        i += 2;
                        continue;
                    case 't':
                        sb.append('\t');
                        i += 2;
                        continue;
                    default:
                        sb.append(c);
                        i++;
                        continue;
                }
            }
            sb.append(c);
            i++;
        }
        return sb.toString();
    }
}
