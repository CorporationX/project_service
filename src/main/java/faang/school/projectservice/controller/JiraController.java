package faang.school.projectservice.controller;

import faang.school.projectservice.dto.jira.request.JiraIssueRequest;
import faang.school.projectservice.dto.jira.response.JiraIssueResponse;
import faang.school.projectservice.dto.jira.response.JiraSearchResponse;
import faang.school.projectservice.gateway.JiraClientGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jira")
@RequiredArgsConstructor
public class JiraController {

    private final JiraClientGateway jiraClientGateway;

    @PostMapping("/issue")
    public ResponseEntity<JiraIssueResponse> createIssue(@RequestBody JiraIssueRequest request) {
        return ResponseEntity.ok(jiraClientGateway.createIssue(request));
    }

    @PutMapping("/issue/{issueIdOrKey}")
    public ResponseEntity<Void> updateIssue(@PathVariable String issueIdOrKey, @RequestBody JiraIssueRequest request) {
        jiraClientGateway.updateIssue(issueIdOrKey, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/issue/{issueIdOrKey}")
    public ResponseEntity<JiraIssueResponse> getIssue(@PathVariable String issueIdOrKey) {
        return ResponseEntity.ok(jiraClientGateway.getIssue(issueIdOrKey));
    }

    @GetMapping("/issues")
    public ResponseEntity<JiraSearchResponse> getIssues(
            @RequestParam("jql") String jqlQuery,
            @RequestParam(value = "startAt", required = false, defaultValue = "0") Integer startAt,
            @RequestParam(value = "maxResults", required = false, defaultValue = "50") Integer maxResults,
            @RequestParam(value = "fields", required = false,
                    defaultValue = "summary,status,assignee,duedate,parent,issuelinks") String fields) {
        return ResponseEntity.ok(jiraClientGateway.searchIssues(jqlQuery, startAt, maxResults, fields));
    }

}