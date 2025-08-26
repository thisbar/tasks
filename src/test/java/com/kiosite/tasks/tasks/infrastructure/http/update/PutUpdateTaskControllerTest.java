package com.kiosite.tasks.tasks.infrastructure.http.update;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PutUpdateTaskControllerTest {

  private static final String USER = "admin";
  private static final String PASS = "secret";

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper mapper;

  private static String json(String s) {
    if (s == null) return "null";
    return "\"" + s.replace("\"", "\\\"") + "\"";
  }

  private String createTask(String title, String description) throws Exception {
    var body =
        """
      {"title": %s, "description": %s}
      """
            .formatted(json(title), json(description));
    MvcResult res =
        mockMvc
            .perform(
                post("/tasks")
                    .with(httpBasic(USER, PASS))
                    .contentType(APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andReturn();
    var node = mapper.readTree(res.getResponse().getContentAsString(StandardCharsets.UTF_8));
    return node.get("id").asText();
  }

  private void updateTask(String id, String title, String description, String status)
      throws Exception {
    String payload =
        """
      {"title": %s, "description": %s, "status": %s}
    """
            .formatted(json(title), json(description), status == null ? "null" : json(status));

    mockMvc
        .perform(
            put("/tasks/{id}", id)
                .with(httpBasic(USER, PASS))
                .contentType(APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(id)));
  }

  @Test
  void should_update_title_and_description() throws Exception {
    String id = createTask("Old", "Old desc");

    var payload = """
      {"title": "New", "description": "New desc", "status": null}
    """;
    mockMvc
        .perform(
            put("/tasks/{id}", id)
                .with(httpBasic(USER, PASS))
                .contentType(APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(id)))
        .andExpect(jsonPath("$.title", is("New")));
  }

  @Test
  void should_fail_400_on_invalid_transition_in_progress_to_done_directly() throws Exception {
    String id = createTask("Flow", "x");
    updateTask(id, "Flow", "x", "IN_PROGRESS");

    var invalid = """
      {"title": "Flow", "description": "x", "status": "DONE"}
    """;
    mockMvc
        .perform(
            put("/tasks/{id}", id)
                .with(httpBasic(USER, PASS))
                .contentType(APPLICATION_JSON)
                .content(invalid))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_allow_reverse_from_done_to_in_progress() throws Exception {
    String id = createTask("Reverse", "ok");

    updateTask(id, "Reverse", "ok", "IN_PROGRESS");
    updateTask(id, "Reverse", "ok", "READY_FOR_REVIEW");
    updateTask(id, "Reverse", "ok", "DONE");

    var back = """
      {"title": "Reverse", "description": "ok", "status": "IN_PROGRESS"}
    """;
    mockMvc
        .perform(
            put("/tasks/{id}", id)
                .with(httpBasic(USER, PASS))
                .contentType(APPLICATION_JSON)
                .content(back))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("IN_PROGRESS")));
  }
}
