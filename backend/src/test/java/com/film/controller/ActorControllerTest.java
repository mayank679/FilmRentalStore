package com.film.controller;

import com.film.dto.ActorDTO;
import com.film.dto.ActorResponseDTO;
import com.film.entity.Actor;
import com.film.services.ActorService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActorController.class)
class ActorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActorService actorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllActors() throws Exception {
        ActorResponseDTO actor = new ActorResponseDTO();
        actor.setActorId(1);

        when(actorService.getAllActors()).thenReturn(List.of(actor));

        mockMvc.perform(get("/api/actors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actorId").value(1));
    }

    @Test
    void testGetActorById() throws Exception {
        ActorResponseDTO actor = new ActorResponseDTO();
        actor.setActorId(1);

        when(actorService.getActorById(1)).thenReturn(actor);

        mockMvc.perform(get("/api/actors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actorId").value(1));
    }

    @Test
    void testGetByFirstName() throws Exception {
        ActorResponseDTO actor = new ActorResponseDTO();
        actor.setActorId(1);

        when(actorService.getByFirstName("John")).thenReturn(List.of(actor));

        mockMvc.perform(get("/api/actors/search/first-name")
                        .param("firstName", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actorId").value(1));
    }

    @Test
    void testGetByLastName() throws Exception {
        ActorResponseDTO actor = new ActorResponseDTO();
        actor.setActorId(1);

        when(actorService.getByLastName("Doe")).thenReturn(List.of(actor));

        mockMvc.perform(get("/api/actors/search/last-name")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actorId").value(1));
    }

    @Test
    void testGetByFullName() throws Exception {
        ActorResponseDTO actor = new ActorResponseDTO();
        actor.setActorId(1);

        when(actorService.getByFirstAndLastName("John", "Doe")).thenReturn(actor);

        mockMvc.perform(get("/api/actors/search/full-name")
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actorId").value(1));
    }

    @Test
    void testGetByLastUpdateRange() throws Exception {
        ActorResponseDTO actor = new ActorResponseDTO();
        actor.setActorId(1);

        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();

        when(actorService.getByLastUpdateRange(from, to)).thenReturn(List.of(actor));

        mockMvc.perform(get("/api/actors/search/last-update/range")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actorId").value(1));
    }

    @Test
    void testGetUpdatedAfter() throws Exception {
        ActorResponseDTO actor = new ActorResponseDTO();
        actor.setActorId(1);

        LocalDateTime from = LocalDateTime.now().minusDays(1);

        when(actorService.getUpdatedAfter(from)).thenReturn(List.of(actor));

        mockMvc.perform(get("/api/actors/search/last-update/after")
                        .param("from", from.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actorId").value(1));
    }

    @Test
    void testGetUpdatedBefore() throws Exception {
        ActorResponseDTO actor = new ActorResponseDTO();
        actor.setActorId(1);

        LocalDateTime to = LocalDateTime.now();

        when(actorService.getUpdatedBefore(to)).thenReturn(List.of(actor));

        mockMvc.perform(get("/api/actors/search/last-update/before")
                        .param("to", to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actorId").value(1));
    }

    @Test
    void testCreateActor() throws Exception {
        ActorDTO request = new ActorDTO();
        request.setFirstName("John");
        request.setLastName("Doe");

        Actor actor = new Actor();
        actor.setActorId(1);

        when(actorService.createActor(request)).thenReturn(actor);

        mockMvc.perform(post("/api/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.actorId").value(1));
    }

    @Test
    void testReplaceActor() throws Exception {
        ActorDTO request = new ActorDTO();
        request.setFirstName("John");
        request.setLastName("Doe");

        Actor actor = new Actor();
        actor.setActorId(1);

        when(actorService.replaceActor(1, request)).thenReturn(actor);

        mockMvc.perform(put("/api/actors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actorId").value(1));
    }

    @Test
    void testPatchActor() throws Exception {
        ActorDTO request = new ActorDTO();
        request.setFirstName("Updated");

        Actor actor = new Actor();
        actor.setActorId(1);

        when(actorService.patchActor(1, request)).thenReturn(actor);

        mockMvc.perform(patch("/api/actors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actorId").value(1));
    }
}