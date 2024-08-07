package faang.school.projectservice.controller;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import faang.school.projectservice.dto.issue.IssueDto;
import faang.school.projectservice.service.JiraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jira/issue")
@RequiredArgsConstructor
public class JiraController {
    private final JiraService jiraService;

    @PostMapping("/{projectKey}")
    public ResponseEntity<String> createIssue(
            @PathVariable String projectKey, @RequestBody IssueDto issueDto) {
        return jiraService.createIssue(projectKey, issueDto);
    }

    @PutMapping("/{issueKey}/description")
    public ResponseEntity<String> updateIssueDescription(
            @PathVariable String issueKey, @RequestParam String description) {
        return jiraService.updateIssueDescription(issueKey, description);
    }

    @PutMapping("/{issueKey}/status")
    public ResponseEntity<String> updateIssueStatus(
            @PathVariable String issueKey, @RequestBody int statusId) {
        return jiraService.updateIssueStatus(issueKey, statusId);
    }

    @PutMapping("/{issueKey}/parent")
    public ResponseEntity<String> updateIssueParent
            (@PathVariable String issueKey, @RequestBody String parentKey) {
        return jiraService.updateIssueParent(issueKey, parentKey);
    }

    @GetMapping("/{issueKey}")
    public IssueDto getIssueDto(@PathVariable String issueKey) {
        return jiraService.getIssueDto(issueKey);
    }

    @DeleteMapping("/{issueKey}")
    public ResponseEntity<String> deleteIssue(
            @PathVariable String issueKey, @RequestParam boolean deleteSubtask) {
        return jiraService.deleteIssue(issueKey, deleteSubtask);
    }

    @GetMapping
    public Iterable<IssueDto> searchIssues(@RequestParam String jql) {
        return jiraService.searchIssues(jql);
    }

    @PatchMapping("/{issueKey}/comment")
    public ResponseEntity<String> addComment(@PathVariable String issueKey, @RequestParam String comment) {
        return jiraService.addComment(issueKey, comment);
    }

}