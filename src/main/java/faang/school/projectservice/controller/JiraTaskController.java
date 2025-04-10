package faang.school.projectservice.controller;

import faang.school.projectservice.dto.jiratask.JiraStatusUpdateRequest;
import faang.school.projectservice.dto.jiratask.JiraTaskCreateRequest;
import faang.school.projectservice.dto.jiratask.JiraTaskResponse;
import faang.school.projectservice.dto.jiratask.JiraTaskUpdateRequest;
import faang.school.projectservice.service.JiraTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jira-tasks")
public class JiraTaskController {

    private final JiraTaskService jiraTaskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<JiraTaskResponse> createJiraTask(@Valid @RequestBody JiraTaskCreateRequest request) {
        return jiraTaskService.createJiraTask(request);
    }

    @PutMapping("/{issueKey}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Void>> updateJiraTask(@PathVariable String issueKey,
                                                     @Valid @RequestBody JiraTaskUpdateRequest request) {
        return jiraTaskService.updateJiraTask(issueKey, request);
    }

    @PatchMapping("/status/{issueKey}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> updateStatusJiraTask(@PathVariable String issueKey,
                                           @Valid @RequestBody JiraStatusUpdateRequest request) {
        return jiraTaskService.updateStatusJiraTask(issueKey, request);
    }

    @GetMapping("/all-filtered")
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<JiraTaskResponse>> getProjectJiraTasksByFilters(
            @RequestParam String projectId, @RequestParam String status, @RequestParam String assignee) {
        return jiraTaskService.getProjectJiraTasksByFilters(projectId, status, assignee);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<JiraTaskResponse>> getProjectJiraTasks(@RequestParam String projectId) {
        return jiraTaskService.getProjectJiraTasks(projectId);
    }

    @GetMapping("/{issueKey}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<JiraTaskResponse> getTaskById(@PathVariable String issueKey) {
        return jiraTaskService.getTaskById(issueKey);
    }
}
