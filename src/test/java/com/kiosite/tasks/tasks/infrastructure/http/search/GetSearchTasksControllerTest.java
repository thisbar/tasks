package com.kiosite.tasks.tasks.infrastructure.http.search;

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
class GetSearchTasksControllerTest {

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
        .andExpect(status().isOk());
  }

  @Test
  void should_list_tasks_filtered_by_status() throws Exception {
    String a = createTask("A", "x");
    String b = createTask("B", "x");
    String c = createTask("C", "x");

    updateTask(a, "A", "x", "IN_PROGRESS");
    updateTask(b, "B", "x", "IN_PROGRESS");

    mockMvc
        .perform(get("/tasks").with(httpBasic(USER, PASS)).param("status", "IN_PROGRESS"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
        .andExpect(jsonPath("$[*].status", everyItem(is("IN_PROGRESS"))));
  }
}
