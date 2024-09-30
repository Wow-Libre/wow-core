package com.register.wowlibre.infrastructure.filter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private final PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
    private ServletOutputStream servletOutputStream;
    private boolean usingWriter = false;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public byte[] getByteArray() {
        try {
            printWriter.flush();
            if (servletOutputStream != null) {
                servletOutputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error flushing streams", e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (usingWriter) {
            throw new IllegalStateException("getWriter() has already been called");
        }
        if (servletOutputStream == null) {
            servletOutputStream = new ServletOutputStream() {
                @Override
                public void write(int b) {
                    byteArrayOutputStream.write(b);
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
                    // No implementation needed
                }
            };
        }
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() {
        if (servletOutputStream != null) {
            throw new IllegalStateException("getOutputStream() has already been called");
        }
        usingWriter = true;
        return printWriter;
    }

    @Override
    public String toString() {
        return byteArrayOutputStream.toString();
    }
}
