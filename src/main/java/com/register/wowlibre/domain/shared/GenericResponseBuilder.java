package com.register.wowlibre.domain.shared;

import org.springframework.http.HttpStatus;

public class GenericResponseBuilder<D> {
    private final GenericResponse<D> genericResponse;

    public GenericResponseBuilder(D data, String transactionId) {
        this.genericResponse = new GenericResponse<>();
        this.genericResponse.setData(data);
        this.genericResponse.setTransactionId(transactionId);
    }

    public GenericResponseBuilder(String transactionId) {
        this.genericResponse = new GenericResponse<>();
        this.genericResponse.setTransactionId(transactionId);
    }

    public GenericResponseBuilder<D> ok() {
        this.genericResponse.setMessage(HttpStatus.OK.getReasonPhrase());
        this.genericResponse.setCode(HttpStatus.OK.value());
        return this;
    }

    public GenericResponseBuilder<D> notContent() {
        return this;
    }

    public GenericResponseBuilder<D> created() {
        this.genericResponse.setMessage(HttpStatus.CREATED.getReasonPhrase());
        this.genericResponse.setCode(HttpStatus.CREATED.value());
        return this;
    }

    public GenericResponseBuilder<D> ok(D data) {
        this.genericResponse.setData(data);
        this.genericResponse.setMessage(HttpStatus.OK.getReasonPhrase());
        this.genericResponse.setCode(HttpStatus.OK.value());

        return this;
    }

    public GenericResponse<D> build() {
        return this.genericResponse;
    }
}
