package com.film.exception;

public class LanguageNotFoundException extends RuntimeException {

    public LanguageNotFoundException(Integer languageId) {
        super("Language not found with ID: " + languageId);
    }
}