package com.film.dto;

public class LanguageDTO {

    private Integer id;
    private String name;

    public LanguageDTO() {}

    public LanguageDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}