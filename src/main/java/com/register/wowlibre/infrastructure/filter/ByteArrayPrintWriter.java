package com.register.wowlibre.infrastructure.filter;

import jakarta.servlet.ServletOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class ByteArrayPrintWriter {
  private ByteArrayOutputStream biteArray = new ByteArrayOutputStream();

  private PrintWriter pw = new PrintWriter(biteArray);

  private ServletOutputStream sos = new ByteArrayServletStream(biteArray);

  public PrintWriter getWriter() {
    return pw;
  }

  public ServletOutputStream getStream() {
    return sos;
  }

  public byte[] toByteArray() {
    return biteArray.toByteArray();
  }
}
