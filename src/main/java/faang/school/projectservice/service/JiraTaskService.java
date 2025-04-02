package faang.school.projectservice.service;

import faang.school.projectservice.client.JiraClient;
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
        return jiraClient.createJiraTask(request);
    }

    public Mono<Void> updateJiraTask(String issueKey, JiraTaskUpdateRequest request) {
        return jiraClient.updateJiraTask(issueKey, request);
    }

    public Mono<List<JiraTaskResponse>> getProjectJiraTasksByFilters() {
        return jiraClient.getProjectJiraTasksByFilters();
    }

    public Mono<List<JiraTaskResponse>> getProjectJiraTasks() {
        return jiraClient.getProjectJiraTasks();
    }

    public Mono<JiraTaskResponse> getTaskById(String issueKey) {
        return jiraClient.getJiraTaskById(issueKey);
    }

}
