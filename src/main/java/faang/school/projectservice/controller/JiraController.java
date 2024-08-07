package faang.school.projectservice.controller;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import faang.school.projectservice.service.JiraService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jira")
@RequiredArgsConstructor
public class JiraController {
    private final JiraService jiraService;

    @PostMapping("/create")
    public String createIssue(@RequestParam String projectKey, @RequestParam Long issueType, @RequestParam String issueSummary) {
        return jiraService.createIssue(projectKey, issueType, issueSummary);
    }

    @GetMapping("/issue/{issueKey}")
    public Issue getIssue(@PathVariable String issueKey) {
        return jiraService.getIssue(issueKey);
    }

    @PutMapping("/issue/{issueKey}")
    public void updateIssue(@PathVariable String issueKey, @RequestBody IssueInputBuilder issueInputBuilder) {
        IssueInput issueInput = issueInputBuilder.build();
        jiraService.updateIssue(issueKey, issueInput);
    }

    @GetMapping("/search")
    public Iterable<Issue> searchIssues(@RequestParam String jql) {
        return jiraService.searchIssues(jql);
    }
}