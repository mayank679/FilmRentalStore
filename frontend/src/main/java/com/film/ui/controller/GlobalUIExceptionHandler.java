package com.film.ui.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Global exception handler for the Thymeleaf frontend.
 * Prevents Whitelabel Error Page from ever appearing.
 * Catches any unhandled exception and redirects to root with an error popup.
 */
@ControllerAdvice
public class GlobalUIExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage",
                "Something went wrong: " + ex.getMessage());
        return "redirect:/";
    }
}
