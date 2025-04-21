package faang.school.projectservice.controller;

import faang.school.projectservice.dto.jiratask.JiraStatusUpdateRequest;
import faang.school.projectservice.dto.jiratask.JiraTaskCreateRequest;
import faang.school.projectservice.dto.jiratask.JiraTaskResponse;
import faang.school.projectservice.dto.jiratask.JiraTaskUpdateRequest;
import faang.school.projectservice.dto.jiratask.task.JiraCreateFieldsDto;
import faang.school.projectservice.dto.jiratask.task.JiraTransitionDto;
import faang.school.projectservice.dto.jiratask.task.JiraUpdateFieldsDto;
import faang.school.projectservice.dto.jiratask.task.fields.JiraAssigneeDto;
import faang.school.projectservice.dto.jiratask.task.fields.JiraIssueTypeDto;
import faang.school.projectservice.dto.jiratask.task.fields.JiraProjectDto;
import faang.school.projectservice.service.JiraTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;


@WebFluxTest
@ContextConfiguration(classes = JiraTaskController.class)
public class JiraTaskControllerTest {

    private final String projectKey = "SCRUM";
    private final String issueKey = "SCRUM-1";

    @MockBean
    private JiraTaskService jiraTaskService;

    @Autowired
    private WebTestClient webClient;

    @Test
    void testPositiveCreateJiraTask() {
        JiraTaskCreateRequest request = createRequestOnCreate();
        JiraTaskResponse response = createResponse();
        when(jiraTaskService.createJiraTask(request)).thenReturn(Mono.just(response));

        webClient.post()
                .uri("/jira-tasks")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(JiraTaskResponse.class)
                .isEqualTo(response);
    }

    @Test
    void testPositiveUpdateJiraTask() {
        JiraTaskUpdateRequest request = createRequestOnUpdate();
        when(jiraTaskService.updateJiraTask(issueKey, request)).thenReturn(Mono.empty());

        webClient.put()
                .uri("/jira-tasks/{issueKey}", issueKey)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    void testPositiveUpdateStatusJiraTask() {
        JiraStatusUpdateRequest request = createStatusUpdateRequest();
        when(jiraTaskService.updateStatusJiraTask(issueKey, request)).thenReturn(Mono.empty());

        webClient.patch()
                .uri("/jira-tasks/status/{issueKey}", issueKey)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    void testPositiveGetProjectJiraTasksByFilters() {
        String status = "To Do";
        String assigneeId = "id-assignee-user";
        List<JiraTaskResponse> listResponse = List.of(createResponse(), createResponse(), createResponse());
        when(jiraTaskService.getProjectJiraTasksByFilters(projectKey, status, assigneeId))
                .thenReturn(Mono.just(listResponse));

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/jira-tasks/all-filtered")
                        .queryParam("projectId", projectKey)
                        .queryParam("status", status)
                        .queryParam("assignee", assigneeId)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(JiraTaskResponse.class)
                .isEqualTo(listResponse)
                .hasSize(listResponse.size());
    }

    @Test
    void testPositiveGetProjectJiraTasks() {
        List<JiraTaskResponse> listResponse = List.of(createResponse(), createResponse(), createResponse());
        when(jiraTaskService.getProjectJiraTasks(projectKey)).thenReturn(Mono.just(listResponse));

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/jira-tasks/all")
                        .queryParam("projectId", projectKey)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(JiraTaskResponse.class)
                .isEqualTo(listResponse)
                .hasSize(listResponse.size());
    }

    @Test
    void testPositiveGetJiraTaskById() {
        JiraTaskResponse response = createResponse();
        when(jiraTaskService.getTaskById(issueKey)).thenReturn(Mono.just(response));

        webClient.get()
                .uri("/jira-tasks/{issueKey}", issueKey)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JiraTaskResponse.class)
                .isEqualTo(response);
    }

    private JiraTaskCreateRequest createRequestOnCreate() {
        return new JiraTaskCreateRequest(JiraCreateFieldsDto.builder()
                .project(new JiraProjectDto(projectKey))
                .summary("Typical Jira task")
                .issueType(new JiraIssueTypeDto("Task"))
                .build());
    }

    private JiraTaskUpdateRequest createRequestOnUpdate() {
        return JiraTaskUpdateRequest.builder()
                .fields(JiraUpdateFieldsDto.builder()
                        .summary("Typical Jira task (updated)")
                        .assignee(new JiraAssigneeDto("id-assignee-user"))
                        .labels(List.of("spring", "web-flux"))
                        .build())
                .build();
    }

    private JiraStatusUpdateRequest createStatusUpdateRequest() {
        return new JiraStatusUpdateRequest(new JiraTransitionDto("21"));
    }

    private JiraTaskResponse createResponse() {
        return JiraTaskResponse.builder()
                .id("10000")
                .key(issueKey)
                .selfUrl("https://test.atlassian.net/")
                .build();
    }
}
