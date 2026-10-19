package com.energy.reading;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MeterNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleMeterNotFound(MeterNotFoundException exception) {
        return ResponseEntity.status(NOT_FOUND).body(Map.of("error", exception.getMessage()));
    }
}
