package com.register.wowlibre.infrastructure.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestWrapper extends HttpServletRequestWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestWrapper.class);

    private final Map<String, String> headers = new ConcurrentHashMap<>();
    private ByteArrayOutputStream outputStream;

    public RequestWrapper(HttpServletRequest request) {
        super(request);
        try {
            // Copy headers
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.put(headerName, request.getHeader(headerName));
            }

            // Copy body
            outputStream = new ByteArrayOutputStream();
            IOUtils.copy(request.getInputStream(), outputStream);
        } catch (IOException e) {
            LOGGER.error("Error in RequestWrapper constructor: {}", e.getMessage(), e);
        }
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray())));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return inputStream.available() <= 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                // No implementation needed
            }

            @Override
            public int read() {
                return inputStream.read();
            }

            @Override
            public int readLine(byte[] b, int off, int len) {
                return inputStream.read(b, off, len);
            }
        };
    }

    public void setHeader(final String name, final String value) {
        headers.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> values = Collections.list(super.getHeaders(name));
        if (headers.containsKey(name)) {
            values.add(0, headers.get(name));
        }
        return Collections.enumeration(values);
    }

    public void removeHeader(final String name) {
        headers.remove(name);
    }

    public void setBody(String body) {
        outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(body.getBytes());
        } catch (IOException e) {
            LOGGER.error("Error setting body: {}", e.getMessage(), e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                LOGGER.error("Error closing outputStream: {}", e.getMessage(), e);
            }
        }
    }

    public String getBody() {
        return outputStream.toString();
    }
}
