package faang.school.projectservice.controller;

import faang.school.projectservice.service.JiraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Task controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectTaskController {
    private final JiraService jiraService;

    @Operation(summary = "Create task", parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)})
    @PostMapping("/tasks")
    public String createTask(@RequestBody String taskJson, long performerUserId, long projectId) {
        return jiraService.createIssue(taskJson, performerUserId, projectId);
    }

    @Operation(summary = "Update task")
    @PutMapping("/tasks/{issueKey}")
    public String updateTask(@PathVariable String issueKey, @RequestBody String taskJson) {
        return jiraService.updateIssue(issueKey, taskJson);
    }

    @Operation(summary = "Get all task")
    @GetMapping("/tasks")
    public String getAllTasks(@RequestParam(required = false) String status, @RequestParam(required = false) String assignee) {
        String jql = "project = BJS2";
        if (status != null) {
            jql += " AND status = \"" + status + "\"";
        }
        if (assignee != null) {
            jql += " AND assignee = \"" + assignee + "\"";
        }

        return jiraService.getIssues(jql);
    }

    @Operation(summary = "Get task")
    @GetMapping("/tasks/{issueId}")
    public String getTaskById(@PathVariable String issueId) {
        return jiraService.getIssueById(issueId);
    }
}
