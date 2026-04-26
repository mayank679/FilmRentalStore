package com.film.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmTextDTO {
	    @NotNull(message = "film id is important")
		private Integer filmId;
	    @NotBlank(message = "title is required")
	    @Size(max = 255, message = "title must not exceed 255 characters")
	    private String title;
	 
	    private String description;
}
