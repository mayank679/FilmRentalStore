package com.film.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActorResponseDTO {

    private Integer       actorId;
    private String        firstName;
    private String        lastName;
    private LocalDateTime lastUpdate;
}