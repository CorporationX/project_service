package faang.school.projectservice.service;

import faang.school.projectservice.client.JiraClient;
import faang.school.projectservice.dto.jiratask.JiraStatusUpdateRequest;
import faang.school.projectservice.dto.jiratask.JiraTaskCreateRequest;
import faang.school.projectservice.dto.jiratask.JiraTaskResponse;
import faang.school.projectservice.dto.jiratask.JiraTaskUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class JiraTaskService {

    private final JiraClient jiraClient;

    public Mono<JiraTaskResponse> createJiraTask(JiraTaskCreateRequest request) {
        printLogMessage("create");
        return jiraClient.createJiraTask(request);
    }

    public Mono<Void> updateJiraTask(String issueKey, JiraTaskUpdateRequest request) {
        printLogMessage("update");
        return jiraClient.updateJiraTask(issueKey, request);
    }

    public Mono<Void> updateStatusJiraTask(String issueKey, JiraStatusUpdateRequest request) {
        printLogMessage("status update");
        return jiraClient.updateStatusJiraTask(issueKey, request);
    }

    public Mono<List<JiraTaskResponse>> getProjectJiraTasksByFilters(String projectId, String status, String assignee) {
        printLogMessage("get all filtered tasks");
        return jiraClient.getProjectJiraTasksByFilters(projectId, status, assignee);
    }

    public Mono<List<JiraTaskResponse>> getProjectJiraTasks(String projectId) {
        printLogMessage("get all tasks");
        return jiraClient.getProjectJiraTasks(projectId);
    }

    public Mono<JiraTaskResponse> getTaskById(String issueKey) {
        printLogMessage("get task by id");
        return jiraClient.getJiraTaskById(issueKey);
    }

    private void printLogMessage(String request) {
        log.debug("Sending [{}] request on Jira Client", request);
    }
}
