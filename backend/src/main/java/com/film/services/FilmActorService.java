package com.film.services;

import com.film.dto.FilmActorDTO;
import com.film.dto.FilmActorResponseDTO;
import com.film.entity.Actor;
import com.film.entity.Film;
import com.film.entity.FilmActor;
import com.film.entity.FilmActorId;
import com.film.exception.DuplicateResourceException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.ActorRepository;
import com.film.repository.FilmActorRepository;
import com.film.repository.FilmRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmActorService {

    private final FilmActorRepository filmActorRepository;
    private final FilmRepository      filmRepository;
    private final ActorRepository     actorRepository;

    @PersistenceContext
    private EntityManager entityManager;


    // ── Helper: entity → response DTO ───────────────────────────────
    private FilmActorResponseDTO toResponse(FilmActor fa) {
        return FilmActorResponseDTO.builder()
                .actorId(fa.getId().getActorId())
                .filmId(fa.getId().getFilmId())
                .filmTitle(fa.getFilm().getTitle())
                .actorFirstName(fa.getActor().getFirstName())
                .actorLastName(fa.getActor().getLastName())
                .lastUpdate(fa.getLastUpdate())
                .build();
    }


    // ── GET all paginated ────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<FilmActorResponseDTO> findAllPaginated(int page, int size) {
        return filmActorRepository.findAll(PageRequest.of(page, size))
                .map(this::toResponse);
    }

    // ── GET all ──────────────────────────────────────────────────────
    public List<FilmActorResponseDTO> getAll() {
        return filmActorRepository.findAll().stream()
                .map(this::toResponse).toList();
    }


    // ── GET by composite key ─────────────────────────────────────────
    public FilmActorResponseDTO getById(Integer actorId, Integer filmId) {
        FilmActorId key = new FilmActorId(actorId, filmId);
        FilmActor fa = filmActorRepository.findById(key)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FilmActor", "actorId + filmId", actorId + " + " + filmId));
        return toResponse(fa);
    }


    // ── GET all actors for a film ────────────────────────────────────
    public List<FilmActorResponseDTO> getByFilm(Integer filmId) {
        if (!filmRepository.existsById(filmId))
            throw new ResourceNotFoundException("Film", "filmId", filmId);
        return filmActorRepository.findByFilm_FilmId(filmId).stream()
                .map(this::toResponse).toList();
    }


    // ── GET all films for an actor ───────────────────────────────────
    public List<FilmActorResponseDTO> getByActor(Integer actorId) {
        if (!actorRepository.existsById(actorId))
            throw new ResourceNotFoundException("Actor", "actorId", actorId);
        return filmActorRepository.findByActor_ActorId(actorId).stream()
                .map(this::toResponse).toList();
    }


    // ── POST — assign actor to film ──────────────────────────────────
    @Transactional
    public FilmActorResponseDTO create(FilmActorDTO dto) {
        FilmActorId key = new FilmActorId(dto.getActorId(), dto.getFilmId());

        // Guard: composite key already exists → 409
        if (filmActorRepository.existsById(key)) {
            throw new DuplicateResourceException(
                    "FilmActor");
        }

        Actor actor = actorRepository.findById(dto.getActorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Actor", "actorId", dto.getActorId()));

        Film film = filmRepository.findById(dto.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Film", "filmId", dto.getFilmId()));

        FilmActor filmActor = FilmActor.builder()
                .id(key)
                .actor(actor)
                .film(film)
                .lastUpdate(LocalDateTime.now())  
                .build();

        FilmActor saved = filmActorRepository.save(filmActor);

        // Refresh to populate lastUpdate (set by DB DEFAULT CURRENT_TIMESTAMP)
        entityManager.flush();
        entityManager.refresh(saved);

        return toResponse(saved);
    }


    // ── PUT — reassign (change actor or film on existing record) ─────
    @Transactional
    public FilmActorResponseDTO replace(Integer actorId, Integer filmId,
                                        FilmActorDTO dto) {
        FilmActorId oldKey = new FilmActorId(actorId, filmId);
        FilmActorId newKey = new FilmActorId(dto.getActorId(), dto.getFilmId());

        // Load existing record
        FilmActor existing = filmActorRepository.findById(oldKey)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FilmActor", "actorId + filmId", actorId + " + " + filmId));

        // If the key is actually changing, guard for duplicate
        if (!oldKey.equals(newKey) && filmActorRepository.existsById(newKey)) {
            throw new DuplicateResourceException(
                    "FilmActor");
        }

        Actor newActor = actorRepository.findById(dto.getActorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Actor", "actorId", dto.getActorId()));

        Film newFilm = filmRepository.findById(dto.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Film", "filmId", dto.getFilmId()));

        // With composite key: delete old row, insert new one
        filmActorRepository.delete(existing);
        filmActorRepository.flush();

        FilmActor updated = FilmActor.builder()
                .id(newKey)
                .actor(newActor)
                .film(newFilm)
                .build();

        FilmActor saved = filmActorRepository.save(updated);
        entityManager.flush();
        entityManager.refresh(saved);

        return toResponse(saved);
    }
}