package com.film.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EmptyResultException extends RuntimeException {

    public EmptyResultException(String message) {
        super(message);
    }

    public EmptyResultException(String fieldName, String fieldValue) {
        super("No films found with " + fieldName + ": " + fieldValue);
    }

    public EmptyResultException(String fieldName1, Object fieldValue1,
                                String fieldName2, Object fieldValue2) {
        super("No films found with " + fieldName1 + ": " + fieldValue1
                + " and " + fieldName2 + ": " + fieldValue2);
    }
}