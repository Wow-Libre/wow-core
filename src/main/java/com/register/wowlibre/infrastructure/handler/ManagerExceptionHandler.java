package com.register.wowlibre.infrastructure.handler;

import com.register.wowlibre.domain.exception.GenericErrorException;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.domain.shared.NotNullValuesDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ManagerExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<GenericResponse<NotNullValuesDto>> methodArgumentNotValidException(MethodArgumentNotValidException e) {

        NotNullValuesDto invalidData = new NotNullValuesDto();
        List<String> errors = new ArrayList<>();

        for (FieldError data : e.getBindingResult().getFieldErrors()) {
            errors.add(String.format("Attribute %s,  %s", data.getField(), data.getDefaultMessage()));
        }

        invalidData.setNumberOfInvalid(e.getBindingResult().getFieldErrorCount());
        invalidData.setValuesInvalid(errors);

        GenericResponse<NotNullValuesDto> response = new GenericResponse<>();
        response.setMessage("Invalid Fields");
        response.setCode(400);
        response.setData(invalidData);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(
            value = {
                    HttpMessageNotReadableException.class
            })
    public ResponseEntity<GenericResponse<Void>> httpMessageNotReadableException() {

        GenericResponse<Void> response = new GenericResponse<>();
        response.setMessage("Check the request body");
        response.setCode(400);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(
            value = {MissingServletRequestParameterException.class
            })
    public ResponseEntity<GenericResponse<Void>> missingServletRequestParameterException(MissingServletRequestParameterException e) {

        GenericResponse<Void> response = new GenericResponse<>();
        response.setMessage(e.getMessage());
        response.setCode(400);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(
            value = {
                    GenericErrorException.class,
            })
    public ResponseEntity<GenericResponse<Void>> unauthorizedException(GenericErrorException e) {
        GenericResponse<Void> response = new GenericResponse<>();
        response.setMessage(e.getMessage() != null ? e.getMessage() : "");
        response.setCode(e.httpStatus.value());
        response.setTransactionId(e.transactionId);
        return ResponseEntity.status(e.httpStatus).body(response);
    }

}
