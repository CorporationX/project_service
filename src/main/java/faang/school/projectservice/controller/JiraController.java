package faang.school.projectservice.controller;

import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.service.jira.JiraService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Jira Issues")
@RestController("/jira")
@RequiredArgsConstructor
public class JiraController {

    private final JiraService jiraService;

    @PostMapping("/issues")
    @ResponseStatus(HttpStatus.CREATED)
    public String createIssue(@Valid @RequestBody IssueDto issueDto) {
        return jiraService.createIssue(issueDto);
    }

    @GetMapping("/issues/{issueKey}")
    public IssueDto getIssue(@PathVariable String issueKey) {
        return jiraService.getIssue(issueKey);
    }

    @GetMapping("/projects/{projectKey}/issues")
    public List<IssueDto> getAllIssues(@PathVariable String projectKey) {
        return jiraService.getAllIssues(projectKey);
    }

    @GetMapping("/projects/{projectKey}/issues/status")
    public List<IssueDto> getIssuesByStatusId(@PathVariable String projectKey, @RequestParam long statusId) {
        return jiraService.getIssuesByStatusId(projectKey, statusId);
    }

    @GetMapping("/projects/{projectKey}/issues/assignee")
    public List<IssueDto> getIssuesByAssigneeId(@PathVariable String projectKey, @RequestParam String assigneeId) {
        return jiraService.getIssuesByAssigneeId(projectKey, assigneeId);
    }
}
