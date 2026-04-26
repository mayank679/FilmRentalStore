package com.film.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActorDTO{
   @NotBlank(message = "First name is required") @Size(max = 45) 
    private String firstName;
   @NotBlank(message = "Last name is required")  @Size(max = 45) 
   private String lastName;
   private String lastUpdate;
   }