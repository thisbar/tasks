package com.kiosite.tasks.tasks.infrastructure.http.create;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PostCreateTaskControllerTest {

  private static final String USER = "admin";
  private static final String PASS = "secret";

  @Autowired MockMvc mockMvc;

  private static String json(String s) {
    if (s == null) return "null";
    return "\"" + s.replace("\"", "\\\"") + "\"";
  }

  @Test
  void should_create_task_and_return_201_location_and_body() throws Exception {
    var body =
        """
      {"title": %s, "description": %s}
      """
            .formatted(json("Task title example"), json("Task description"));

    mockMvc
        .perform(
            post("/tasks").with(httpBasic(USER, PASS)).contentType(APPLICATION_JSON).content(body))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.id", not(isEmptyString())))
        .andExpect(jsonPath("$.title", is("Task title example")))
        .andExpect(jsonPath("$.status", is("PENDING")));
  }

  @Test
  void should_fail_with_400_when_title_is_blank() throws Exception {
    var body = """
      {"title": "   ", "description": "desc"}
    """;

    mockMvc
        .perform(
            post("/tasks").with(httpBasic(USER, PASS)).contentType(APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
        .andExpect(jsonPath("$", isA(java.util.List.class)));
  }
}
