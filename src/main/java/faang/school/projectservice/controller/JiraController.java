package faang.school.projectservice.controller;

import com.atlassian.jira.rest.client.api.domain.Comment;
import faang.school.projectservice.dto.issue.IssueDto;
import faang.school.projectservice.service.JiraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/jira/issues")
public class JiraController {
    private JiraService jiraService;

    @PostMapping("/{projectKey}")
    public ResponseEntity<String> createIssue(@PathVariable String projectKey,
                                              @RequestBody IssueDto issueDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jiraService.createIssue(projectKey, issueDto));
    }

    @PutMapping("/{issueKey}/description")
    public void updateIssueDescription(@PathVariable String issueKey,
                                       @RequestParam String description) {
        jiraService.updateIssueDescription(issueKey, description);
    }

    @PutMapping("/{issueKey}/status")
    public void updateIssueStatus(@PathVariable String issueKey,
                                  @RequestParam int statusId) {
        jiraService.updateIssueStatus(issueKey, statusId);
    }

    @PutMapping("/{issueKey}/{parentKey}")
    public void updateParentIssue(@PathVariable String issueKey,
                                  @PathVariable String parentKey) {
        jiraService.updateParentIssue(issueKey, parentKey);
    }

    @GetMapping("/{projectKey}")
    public ResponseEntity<Iterable<IssueDto>> getAllIssue(@PathVariable String projectKey) {
        return ResponseEntity.status(HttpStatus.OK).body(jiraService.getAllIssue(projectKey));
    }

    @GetMapping("/{issueKey}")
    public ResponseEntity<IssueDto> getIssueDto(@PathVariable String issueKey) {
        return ResponseEntity.status(HttpStatus.OK).body(jiraService.getIssueDto(issueKey));
    }

    @GetMapping("/filters")
    public ResponseEntity<Iterable<IssueDto>> getIssueWithFilter(@RequestParam String jqlFilter) {
        return ResponseEntity.status(HttpStatus.OK).body(jiraService.getIssueWithFilter(jqlFilter));
    }

    @PutMapping("/{issueKey}/comments")
    public void addComment(@PathVariable String issueKey,
                           @RequestParam String commentBody) {
        jiraService.addComment(issueKey, commentBody);
    }

    @GetMapping("/{issueKey}/comments")
    public ResponseEntity<List<Comment>> getAllComments(@PathVariable String issueKey) {
        return ResponseEntity.status(HttpStatus.OK).body(jiraService.getAllComments(issueKey));
    }

    @DeleteMapping("/{issueKey}")
    public void deleteIssue(@PathVariable String issueKey,
                            @RequestParam boolean deleteSubtask) {
        jiraService.deleteIssue(issueKey, deleteSubtask);
    }
}
