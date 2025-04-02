package faang.school.projectservice.controller;

import faang.school.projectservice.dto.jiratask.JiraTaskCreateRequest;
import faang.school.projectservice.dto.jiratask.JiraTaskResponse;
import faang.school.projectservice.dto.jiratask.JiraTaskUpdateRequest;
import faang.school.projectservice.service.JiraTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jira-tasks")
public class JiraTaskController {

    private final JiraTaskService jiraTaskService;

    @PostMapping
    public ResponseEntity<Mono<JiraTaskResponse>> createJiraTask(@RequestBody JiraTaskCreateRequest request) {
        Mono<JiraTaskResponse> response = jiraTaskService.createJiraTask(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{issueKey}")
    public ResponseEntity<Mono<Void>> updateJiraTask(@PathVariable String issueKey,
                                                     @RequestBody JiraTaskUpdateRequest request) {
        Mono<Void> response = jiraTaskService.updateJiraTask(issueKey, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-filtered")
    public ResponseEntity<Mono<List<JiraTaskResponse>>> getProjectJiraTasksByFilters(
            @RequestParam String projectId, @RequestParam String status, @RequestParam String assignee) {
        Mono<List<JiraTaskResponse>> response = jiraTaskService.getProjectJiraTasksByFilters(projectId, status, assignee);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Mono<List<JiraTaskResponse>>> getProjectJiraTasks(@RequestParam String projectId) {
        Mono<List<JiraTaskResponse>> response = jiraTaskService.getProjectJiraTasks(projectId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{issueKey}")
    public ResponseEntity<Mono<JiraTaskResponse>> getTaskById(@PathVariable String issueKey) {
        Mono<JiraTaskResponse> response = jiraTaskService.getTaskById(issueKey);
        return ResponseEntity.ok(response);
    }
}
