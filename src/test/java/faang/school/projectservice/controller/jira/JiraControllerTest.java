package faang.school.projectservice.controller.jira;

import faang.school.projectservice.dto.jira.JiraDto;
import faang.school.projectservice.service.jira.JiraServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

@WebFluxTest(JiraController.class)
@ExtendWith(MockitoExtension.class)
class JiraControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private JiraServiceImpl jiraService;

    private JiraDto jiraDto;
    private JiraDto expectedDto;

    @BeforeEach
    void setUp() {
        jiraDto = new JiraDto();
        expectedDto = new JiraDto();
    }

    @Test
    void getIssue() {
        expectedDto.setKey("Test");

        Mockito.when(jiraService.getIssue(Mockito.anyString()))
                .thenReturn(Mono.just(expectedDto));

        webTestClient.get()
                .uri("/jira/issue1/{issueKey}", "Test")
                .exchange()
                .expectStatus().isOk()
                .expectBody(JiraDto.class)
                .isEqualTo(expectedDto);

        Mockito.verify(jiraService, Mockito.times(1))
                .getIssue(Mockito.anyString());
    }

    @Test
    @DisplayName("Проверка на статус Ок, при валидном dto")
    void createTaskValidationSuccess() {
        JiraDto.Fields fields = new JiraDto.Fields();
        JiraDto.Fields.Project project = new JiraDto.Fields.Project();
        JiraDto.Fields.IssueType issueType = new JiraDto.Fields.IssueType();
        fields.setSummary("sd");
        project.setKey("ds");
        issueType.setName("Ds");
        fields.setProject(project);
        fields.setIssuetype(issueType);
        jiraDto.setFields(fields);

        expectedDto.setId("1");

        Mockito.when(jiraService.createTask(Mockito.any(JiraDto.class)))
                .thenReturn(Mono.just(expectedDto));

        webTestClient.post()
                .uri("/jira/task")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jiraDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JiraDto.class)
                .isEqualTo(expectedDto);

        Mockito.verify(jiraService, Mockito.times(1))
                .createTask(Mockito.any(JiraDto.class));

    }

    @Test
    @DisplayName("Проверка на статус BadRequest при не валидном dto")
    void createTaskValidationFailure() {
        JiraDto.Fields fields = new JiraDto.Fields();
        jiraDto.setFields(fields);

        webTestClient.post()
                .uri("/jira/task")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jiraDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Проверка на статус Ок, при валидном dto")
    void updateTaskValidationSuccess() {
        jiraDto.setKey("Test");

        Mockito.when(jiraService.updateTask(Mockito.any(JiraDto.class)))
                .thenReturn(Mono.just("successfully"));

        webTestClient.put()
                .uri("/jira/task")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jiraDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("\"successfully\"");

        Mockito.verify(jiraService, Mockito.times(1))
                .updateTask(Mockito.any(JiraDto.class));
    }

    @Test
    @DisplayName("Проверка на статус BadRequest при не валидном dto")
    void updateTaskValidationFailure() {
        webTestClient.put()
                .uri("/jira/task")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jiraDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Проверка на статус Ок, при валидном dto")
    void updateTaskLinkValidationSuccess() {
        JiraDto.IssueLinkType issueLinkType = new JiraDto.IssueLinkType();
        JiraDto.OutwardIssue outwardIssue = new JiraDto.OutwardIssue();
        JiraDto.InwardIssue inwardIssue = new JiraDto.InwardIssue();
        issueLinkType.setName("Test");
        outwardIssue.setKey("Test");
        inwardIssue.setKey("Test");

        jiraDto.setType(issueLinkType);
        jiraDto.setInwardIssue(inwardIssue);
        jiraDto.setOutwardIssue(outwardIssue);

        Mockito.when(jiraService.updateTaskLink(Mockito.any(JiraDto.class)))
                .thenReturn(Mono.just("successfully"));

        webTestClient.put()
                .uri("/jira/link")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jiraDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("\"successfully\"");

        Mockito.verify(jiraService, Mockito.times(1))
                .updateTaskLink(Mockito.any(JiraDto.class));
    }

    @Test
    @DisplayName("Проверка на статус BadRequest при не валидном dto")
    void updateTaskLinkValidationFailure() {
        JiraDto.IssueLinkType issueLinkType = new JiraDto.IssueLinkType();
        issueLinkType.setName("Test");
        jiraDto.setType(issueLinkType);

        webTestClient.put()
                .uri("/jira/link")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jiraDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Проверка на статус Ок, при валидном dto")
    void changeTaskStatusValidationSuccess() {
        JiraDto.JiraTransitions jiraTransitions = new JiraDto.JiraTransitions();
        jiraTransitions.setName("Test");
        jiraDto.setTransitions(List.of(jiraTransitions));
        jiraDto.setKey("Test");
        jiraDto.setStatus("Test");

        Mockito.when(jiraService.changeTaskStatus(Mockito.any(JiraDto.class)))
                .thenReturn(Mono.just("successfully"));

        webTestClient.put()
                .uri("/jira/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jiraDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("successfully");

        Mockito.verify(jiraService, Mockito.times(1))
                .changeTaskStatus(Mockito.any(JiraDto.class));
    }

    @Test
    @DisplayName("Проверка на статус BadRequest при не валидном dto")
    void changeTaskStatusValidationFailure() {
        jiraDto.setKey("Test");
        jiraDto.setStatus("Test");

        webTestClient.put()
                .uri("/jira/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jiraDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getAllTasks() {
        expectedDto.setKey("Test");

        Mockito.when(jiraService.getAllTasks(Mockito.anyString()))
                .thenReturn(Mono.just(expectedDto));

        webTestClient.get()
                .uri("/jira/tasks/{keyProject}", "Test")
                .exchange()
                .expectStatus().isOk()
                .expectBody(JiraDto.class)
                .isEqualTo(expectedDto);
    }

    @Test
    void getTaskByFilters() {
        jiraDto.setKey("Test");

        expectedDto.setKey("Test key");

        Mockito.when(jiraService.getTaskByFilters(Mockito.any(JiraDto.class)))
                .thenReturn(Mono.just(expectedDto));

        webTestClient.post()
                .uri("/jira/filters")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jiraDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JiraDto.class)
                .isEqualTo(expectedDto);

        Mockito.verify(jiraService, Mockito.times(1))
                .getTaskByFilters(Mockito.any(JiraDto.class));
    }
}