package com.film.service;

import com.film.services.ActorService;
import com.film.dto.ActorDTO;
import com.film.dto.ActorResponseDTO;
import com.film.entity.Actor;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.ActorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActorServiceTest {

    @Mock
    private ActorRepository actorRepository;

    @InjectMocks
    private ActorService actorService;

    @Test
    void testGetAllActors() {
        Actor actor = new Actor();
        actor.setActorId(1);
        actor.setFirstName("John");
        actor.setLastName("Doe");
        actor.setLastUpdate(LocalDateTime.now());

        when(actorRepository.findAll()).thenReturn(List.of(actor));

        List<ActorResponseDTO> result = actorService.getAllActors();

        assertThat(result).hasSize(1);
    }

    @Test
    void testGetActorById() {
        Actor actor = new Actor();
        actor.setActorId(1);
        actor.setFirstName("John");
        actor.setLastName("Doe");

        when(actorRepository.findById(1)).thenReturn(Optional.of(actor));

        ActorResponseDTO result = actorService.getActorById(1);

        assertThat(result.getActorId()).isEqualTo(1);
    }

    @Test
    void testGetActorById_NotFound() {
        when(actorRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> actorService.getActorById(1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testGetByFirstName() {
        Actor actor = new Actor();
        actor.setFirstName("John");

        when(actorRepository.findByFirstNameContainingIgnoreCase("jo"))
                .thenReturn(List.of(actor));

        List<ActorResponseDTO> result = actorService.getByFirstName("jo");

        assertThat(result).isNotEmpty();
    }

    @Test
    void testGetByFirstName_NotFound() {
        when(actorRepository.findByFirstNameContainingIgnoreCase("x"))
                .thenReturn(List.of());

        assertThatThrownBy(() -> actorService.getByFirstName("x"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testCreateActor() {
        ActorDTO dto = new ActorDTO();
        dto.setFirstName("John");

        Actor saved = new Actor();
        saved.setFirstName("John");

        when(actorRepository.save(any(Actor.class))).thenReturn(saved);

        Actor result = actorService.createActor(dto);

        assertThat(result).isNotNull();
    }

    @Test
    void testGetByLastUpdateRange_InvalidRange() {
        LocalDateTime now = LocalDateTime.now();

        assertThatThrownBy(() ->
                actorService.getByLastUpdateRange(now, now.minusDays(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}