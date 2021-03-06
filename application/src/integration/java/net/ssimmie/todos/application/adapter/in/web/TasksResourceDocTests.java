package net.ssimmie.todos.application.adapter.in.web;

import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToApplicationContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@Tag("docs")
@WebFluxTest(controllers = TasksResource.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class TasksResourceDocTests {

  private WebTestClient webTestClient;

  @BeforeEach
  public void setUp(
      final ApplicationContext applicationContext,
      final RestDocumentationContextProvider restDocumentation) {
    this.webTestClient =
        bindToApplicationContext(applicationContext)
            .configureClient()
            .filter(documentationConfiguration(restDocumentation))
            .build();
  }

  @Test
  public void shouldListAllTasks() {
    this.webTestClient
        .get()
        .uri("/tasks")
        .accept(HAL_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$._links.self.href")
        .isEqualTo("http://localhost:8080/tasks")
        .consumeWith(document("get-tasks",
            links(halLinks(), linkWithRel("self").description("Link to the tasks resource"))));
  }

  @Test
  public void shouldCreateTask() {
    final Task expectedTask = new Task();
    expectedTask.setTodo("derp");
    this.webTestClient
        .post()
        .uri("/tasks")
        .contentType(APPLICATION_JSON)
        .accept(HAL_JSON)
        .bodyValue(expectedTask)
        .exchange()
        .expectBody()
        .jsonPath("$.todo")
        .isEqualTo("derp")
        .jsonPath("$._links.self.href")
        .isEqualTo("http://localhost:8080/tasks")
        .consumeWith(document("create-task",
            links(halLinks(), linkWithRel("self").description("Link to the tasks resource"))));
  }
}
