package com.film.service;

import com.film.services.FilmActorService;
import com.film.dto.FilmActorDTO;
import com.film.dto.FilmActorResponseDTO;
import com.film.entity.*;
import com.film.exception.DuplicateResourceException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.ActorRepository;
import com.film.repository.FilmActorRepository;
import com.film.repository.FilmRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmActorServiceTest {

    @Mock
    private FilmActorRepository filmActorRepository;

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private ActorRepository actorRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private FilmActorService filmActorService;

    private Actor actor;
    private Film film;
    private FilmActor filmActor;
    private FilmActorId id;

    @BeforeEach
    void setup() {
        actor = new Actor();
        actor.setActorId(1);
        actor.setFirstName("John");
        actor.setLastName("Doe");

        film = new Film();
        film.setFilmId(1);
        film.setTitle("Test Film");

        id = new FilmActorId(1, 1);

        filmActor = FilmActor.builder()
                .id(id)
                .actor(actor)
                .film(film)
                .lastUpdate(LocalDateTime.now())
                .build();

        ReflectionTestUtils.setField(filmActorService, "entityManager", entityManager);
    }

    @Test
    void testGetAll() {
        when(filmActorRepository.findAll()).thenReturn(List.of(filmActor));

        List<FilmActorResponseDTO> result = filmActorService.getAll();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getActorId()).isEqualTo(1);
    }

    @Test
    void testGetById() {
        when(filmActorRepository.findById(id)).thenReturn(Optional.of(filmActor));

        FilmActorResponseDTO result = filmActorService.getById(1, 1);

        assertThat(result.getActorId()).isEqualTo(1);
    }

    @Test
    void testGetById_NotFound() {
        when(filmActorRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filmActorService.getById(1, 1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testGetByFilm() {
        when(filmRepository.existsById(1)).thenReturn(true);
        when(filmActorRepository.findByFilm_FilmId(1))
                .thenReturn(List.of(filmActor));

        List<FilmActorResponseDTO> result = filmActorService.getByFilm(1);

        assertThat(result).isNotEmpty();
    }

    @Test
    void testGetByFilm_NotFound() {
        when(filmRepository.existsById(1)).thenReturn(false);

        assertThatThrownBy(() -> filmActorService.getByFilm(1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testGetByActor() {
        when(actorRepository.existsById(1)).thenReturn(true);
        when(filmActorRepository.findByActor_ActorId(1))
                .thenReturn(List.of(filmActor));

        List<FilmActorResponseDTO> result = filmActorService.getByActor(1);

        assertThat(result).isNotEmpty();
    }

    @Test
    void testGetByActor_NotFound() {
        when(actorRepository.existsById(1)).thenReturn(false);

        assertThatThrownBy(() -> filmActorService.getByActor(1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testCreate() {
        FilmActorDTO dto = new FilmActorDTO();
        dto.setActorId(1);
        dto.setFilmId(1);

        when(filmActorRepository.existsById(id)).thenReturn(false);
        when(actorRepository.findById(1)).thenReturn(Optional.of(actor));
        when(filmRepository.findById(1)).thenReturn(Optional.of(film));
        when(filmActorRepository.save(any(FilmActor.class))).thenReturn(filmActor);

        FilmActorResponseDTO result = filmActorService.create(dto);

        assertThat(result).isNotNull();

        verify(entityManager).flush();
        verify(entityManager).refresh(any(FilmActor.class));
    }

    @Test
    void testCreate_Duplicate() {
        FilmActorDTO dto = new FilmActorDTO();
        dto.setActorId(1);
        dto.setFilmId(1);

        when(filmActorRepository.existsById(id)).thenReturn(true);

        assertThatThrownBy(() -> filmActorService.create(dto))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void testReplace() {
        FilmActorDTO dto = new FilmActorDTO();
        dto.setActorId(1);
        dto.setFilmId(1);

        when(filmActorRepository.findById(id)).thenReturn(Optional.of(filmActor));
        when(actorRepository.findById(1)).thenReturn(Optional.of(actor));
        when(filmRepository.findById(1)).thenReturn(Optional.of(film));
        when(filmActorRepository.save(any(FilmActor.class))).thenReturn(filmActor);

        FilmActorResponseDTO result =
                filmActorService.replace(1, 1, dto);

        assertThat(result).isNotNull();

        verify(filmActorRepository).delete(any());
        verify(entityManager).flush();
    }

    @Test
    void testReplace_NotFound() {
        FilmActorDTO dto = new FilmActorDTO();
        dto.setActorId(1);
        dto.setFilmId(1);

        when(filmActorRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                filmActorService.replace(1, 1, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testReplace_Duplicate() {
        FilmActorDTO dto = new FilmActorDTO();
        dto.setActorId(2);
        dto.setFilmId(2);

        FilmActorId newId = new FilmActorId(2, 2);

        when(filmActorRepository.findById(id)).thenReturn(Optional.of(filmActor));
        when(filmActorRepository.existsById(newId)).thenReturn(true);

        assertThatThrownBy(() ->
                filmActorService.replace(1, 1, dto))
                .isInstanceOf(DuplicateResourceException.class);
    }
}