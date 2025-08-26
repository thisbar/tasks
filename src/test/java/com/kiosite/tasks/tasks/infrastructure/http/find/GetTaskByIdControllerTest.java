package com.kiosite.tasks.tasks.infrastructure.http.find;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
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
class GetTaskByIdControllerTest {

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

  @Test
  void should_get_task_by_id() throws Exception {
    String id = createTask("Get me", "please");
    mockMvc
        .perform(get("/tasks/{id}", id).with(httpBasic(USER, PASS)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(id)))
        .andExpect(jsonPath("$.title", is("Get me")));
  }

  @Test
  void should_return_400_on_invalid_uuid() throws Exception {
    mockMvc
        .perform(get("/tasks/{id}", "not-a-uuid").with(httpBasic(USER, PASS)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_return_404_when_not_found() throws Exception {
    String randomId = UUID.randomUUID().toString();
    mockMvc
        .perform(get("/tasks/{id}", randomId).with(httpBasic(USER, PASS)))
        .andExpect(status().isNotFound());
  }
}
