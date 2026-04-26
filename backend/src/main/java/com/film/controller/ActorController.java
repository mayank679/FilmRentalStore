package com.film.controller;


import com.film.dto.ActorDTO;
import com.film.dto.ActorResponseDTO;
import com.film.entity.Actor;
import com.film.services.ActorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import java.util.List;
 
@RestController
@RequestMapping("/api/actors")
@RequiredArgsConstructor
public class ActorController {
 
    private final ActorService actorService;
 
    
    // GET all actors (paginated)
    @GetMapping("/paged")
    public ResponseEntity<Page<ActorResponseDTO>> getAllActorsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(actorService.findAllPaginated(page, size));
    }

    //1.get all actors
    @GetMapping
    public ResponseEntity<List<ActorResponseDTO>> getAllActors() {
        return ResponseEntity.ok(actorService.getAllActors());
    }
 
    
    //2.get actor by id
    //api/actors/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ActorResponseDTO> getActorById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(actorService.getActorById(id));
    }
    
    //3.get actor by first name
    @GetMapping("/search/first-name")
    public ResponseEntity<List<ActorResponseDTO>> getByFirstName(
            @RequestParam String firstName) {
        return ResponseEntity.ok(actorService.getByFirstName(firstName));
    }
    
    //4.get actor by first name
    @GetMapping("/search/last-name")
    public ResponseEntity<List<ActorResponseDTO>> getByLastName(
            @RequestParam String lastName) {
        return ResponseEntity.ok(actorService.getByLastName(lastName));
    }
    
    //5.get actor by full name
    @GetMapping("/search/full-name")
    public ResponseEntity<ActorResponseDTO> getByFullName(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        return ResponseEntity.ok(
                actorService.getByFirstAndLastName(firstName, lastName));
    }
    
    
    //6.get actor by lastupdate range
    @GetMapping("/search/last-update/range")
    public ResponseEntity<List<ActorResponseDTO>> getByLastUpdateRange(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
 
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to) {
        return ResponseEntity.ok(
                actorService.getByLastUpdateRange(from, to));
    }
    
    @GetMapping("/search/last-update/after")
    public ResponseEntity<List<ActorResponseDTO>> getUpdatedAfter(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from) {
        return ResponseEntity.ok(actorService.getUpdatedAfter(from));
    }
 
 
    // ── GET /api/v1/actors/search/last-update/before
    //         ?to=2006-02-28T23:59:59
    //    Returns all actors updated before the given datetime
    @GetMapping("/search/last-update/before")
    public ResponseEntity<List<ActorResponseDTO>> getUpdatedBefore(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to) {
        return ResponseEntity.ok(actorService.getUpdatedBefore(to));
    }
 
    
    
 
    // 9.post a actor
    //api/actors
    @PostMapping
    public ResponseEntity<Actor> createActor(@Valid @RequestBody ActorDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(actorService.createActor(request));
    }
 
    //10.update actor
    //api/actors/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Actor> replaceActor(
            @PathVariable Integer id,
            @Valid @RequestBody ActorDTO request) {
        return ResponseEntity.ok(actorService.replaceActor(id, request));
    }
 
    
    //11.patch actor
    // PATCH /api/actors/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<Actor> patchActor(
            @PathVariable Integer id,
            @RequestBody ActorDTO request) {
        return ResponseEntity.ok(actorService.patchActor(id, request));
    }
}
 