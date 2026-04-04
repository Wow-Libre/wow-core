package com.register.wowlibre.infrastructure.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Inyecta headers sin leer el body. Necesario para {@code multipart/form-data}:
 * {@link RequestWrapper} vacía el {@code InputStream} y Spring no puede enlazar {@code MultipartFile}.
 */
public class HeaderMutatingRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> extraHeaders = new LinkedHashMap<>();

    public HeaderMutatingRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void setHeader(String name, String value) {
        extraHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        if (extraHeaders.containsKey(name)) {
            return extraHeaders.get(name);
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (extraHeaders.containsKey(name)) {
            return Collections.enumeration(Collections.singletonList(extraHeaders.get(name)));
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> names = new HashSet<>(Collections.list(super.getHeaderNames()));
        names.addAll(extraHeaders.keySet());
        return Collections.enumeration(names);
    }
}
