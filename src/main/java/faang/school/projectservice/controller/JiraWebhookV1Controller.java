package faang.school.projectservice.controller;

import faang.school.projectservice.dto.jira.webhook.JiraUpdateIssuePayload;
import faang.school.projectservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/jira/webhooks")
@RequiredArgsConstructor
public class JiraWebhookV1Controller {

    private final TaskService taskService;

    @PostMapping("/issue/{issuekey}")
    @ResponseStatus(HttpStatus.OK)
    public void handleUpdateIssue(
            @PathVariable String issuekey,
            @RequestBody @Validated JiraUpdateIssuePayload payload) {
        taskService.updateTaskByJira(issuekey, payload);
    }
}
