package com.film.services;

import com.film.dto.ActorDTO;
import com.film.dto.ActorResponseDTO;
import com.film.entity.Actor;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.ActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor 
public class ActorService {

    private final ActorRepository actorRepository;

    private ActorResponseDTO toResponse(Actor actor) {
        return ActorResponseDTO.builder()
                .actorId(actor.getActorId())
                .firstName(actor.getFirstName())
                .lastName(actor.getLastName())
                .lastUpdate(actor.getLastUpdate())
                .build();
    }

    // ── GET all actors (paginated) ───────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<ActorResponseDTO> findAllPaginated(int page, int size) {
        return actorRepository.findAll(PageRequest.of(page, size))
                .map(this::toResponse);
    }

    // ── GET all actors ───────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<ActorResponseDTO> getAllActors() {
        return actorRepository.findAll()
                .stream().map(this::toResponse).toList();
    }

    // ── GET actor by ID ──────────────────────────────────────────────
    @Transactional(readOnly = true)
    public ActorResponseDTO getActorById(Integer actorId) {
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Actor", "actorId", actorId));
        return toResponse(actor);
    }

    // ── GET actors by first name (partial, case-insensitive) ─────────
    @Transactional(readOnly = true)
    public List<ActorResponseDTO> getByFirstName(String firstName) {
        List<Actor> results =
                actorRepository.findByFirstNameContainingIgnoreCase(firstName);
        if (results.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Actor", "firstName", firstName);
        }
        return results.stream().map(this::toResponse).toList();
    }

    // ── GET actors by last name (partial, case-insensitive) ─────────
    @Transactional(readOnly = true)
    public List<ActorResponseDTO> getByLastName(String lastName) {
        return actorRepository
                .findByLastNameContainingIgnoreCase(lastName)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── GET actor by exact first name AND last name ──────────────────
    @Transactional(readOnly = true)
    public ActorResponseDTO getByFirstAndLastName(
            String firstName, String lastName) {
        Actor actor = actorRepository
                .findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Actor", "firstName + lastName",
                        firstName + " " + lastName));
        return toResponse(actor);
    }

    // ── GET actors by last_update range ──────────────────────────────
    @Transactional(readOnly = true)
    public List<ActorResponseDTO> getByLastUpdateRange(
            LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "'from' date must be before 'to' date");
        }
        List<Actor> results =
                actorRepository.findByLastUpdateBetween(from, to);
        if (results.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Actor", "lastUpdate range", from + " to " + to);
        }
        return results.stream().map(this::toResponse).toList();
    }

    // ── GET actors updated after a date ──────────────────────────────
    @Transactional(readOnly = true)
    public List<ActorResponseDTO> getUpdatedAfter(LocalDateTime from) {
        return actorRepository.findByLastUpdateAfter(from)
                .stream().map(this::toResponse).toList();
    }

    // ── GET actors updated before a date ─────────────────────────────
    @Transactional(readOnly = true)
    public List<ActorResponseDTO> getUpdatedBefore(LocalDateTime to) {
        return actorRepository.findByLastUpdateBefore(to)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public Actor createActor(ActorDTO request) {
        Actor actor = Actor.builder()
                .firstName(request.getFirstName())
                .lastName(request.getFirstName())
                .lastUpdate(LocalDateTime.now())
                .build();
        return actorRepository.save(actor);
    }

    @Transactional
    public Actor replaceActor(Integer id, ActorDTO request) {
        ActorResponseDTO actor = getActorById(id);
        actor.setFirstName(request.getFirstName());
        actor.setLastName(request.getFirstName());
        actor.setLastUpdate(LocalDateTime.now());
        return actorRepository.save(actor);
    }

    @Transactional
    public Actor patchActor(Integer id, ActorDTO request) {
        ActorResponseDTO actor = getActorById(id);
        if (request.getFirstName() != null) actor.setFirstName(request.getFirstName());
        if (request.getFirstName() != null)  actor.setLastName(request.getFirstName());
        actor.setLastUpdate(LocalDateTime.now());
        return actorRepository.save(actor);
    }
}
