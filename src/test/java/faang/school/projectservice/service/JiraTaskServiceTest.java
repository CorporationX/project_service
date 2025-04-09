package faang.school.projectservice.service;

import faang.school.projectservice.client.JiraClient;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JiraTaskServiceTest {

    private final String projectKey = "SCRUM";
    private final String issueKey = "SCRUM-1";

    @InjectMocks
    private JiraTaskService jiraTaskService;

    @Mock
    private JiraClient jiraClient;

    @Test
    void testPositiveCreateJiraTask() {
        JiraTaskCreateRequest request = createRequestOnCreate();
        JiraTaskResponse response = createResponse();
        when(jiraClient.createJiraTask(request)).thenReturn(Mono.just(response));

        Mono<JiraTaskResponse> result = jiraTaskService.createJiraTask(request);

        assertEquals(result.block(), response);
    }

    @Test
    void testPositiveUpdateJiraTask() {
        JiraTaskUpdateRequest request = createRequestOnUpdate();
        Mono<ResponseEntity<Void>> response = Mono.empty();
        when(jiraClient.updateJiraTask(issueKey, request)).thenReturn(response);

        Mono<ResponseEntity<Void>> result = jiraTaskService.updateJiraTask(issueKey, request);

        assertEquals(result, response);
        result.block();
    }

    @Test
    void testPositiveUpdateStatusJiraTask() {
        JiraStatusUpdateRequest request = createStatusUpdateRequest();
        Mono<Void> response = Mono.empty();
        when(jiraClient.updateStatusJiraTask(issueKey, request)).thenReturn(response);

        Mono<Void> result = jiraTaskService.updateStatusJiraTask(issueKey, request);

        assertEquals(result, response);
        result.block();
    }

    @Test
    void testPositiveGetProjectJiraTasksByFilters() {
        String status = "To Do";
        String assigneeId = "id-assignee-user";
        List<JiraTaskResponse> listResponse = List.of(createResponse(), createResponse(), createResponse());
        Mono<List<JiraTaskResponse>> response = Mono.just(listResponse);
        when(jiraClient.getProjectJiraTasksByFilters(projectKey, status, assigneeId)).thenReturn(response);

        Mono<List<JiraTaskResponse>> result =
                jiraTaskService.getProjectJiraTasksByFilters(projectKey, status, assigneeId);

        assertEquals(result, response);
        result.block();
    }

    @Test
    void testPositiveGetProjectJiraTasks() {
        List<JiraTaskResponse> listResponse = List.of(createResponse(), createResponse(), createResponse());
        Mono<List<JiraTaskResponse>> response = Mono.just(listResponse);
        when(jiraClient.getProjectJiraTasks(projectKey)).thenReturn(response);

        Mono<List<JiraTaskResponse>> result =
                jiraTaskService.getProjectJiraTasks(projectKey);

        assertEquals(result, response);
        result.block();
    }

    @Test
    void testPositiveGetJiraTaskById() {
        JiraTaskResponse response = createResponse();
        when(jiraClient.getJiraTaskById(issueKey)).thenReturn(Mono.just(response));

        Mono<JiraTaskResponse> result =
                jiraTaskService.getTaskById(issueKey);

        assertEquals(result.block(), response);
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
