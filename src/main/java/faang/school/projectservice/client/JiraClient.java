package faang.school.projectservice.client;

import faang.school.projectservice.config.JiraConfig;
import faang.school.projectservice.dto.jira.request.JiraIssueRequest;
import faang.school.projectservice.dto.jira.response.JiraIssueResponse;
import faang.school.projectservice.dto.jira.response.JiraSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "jiraClient", url = "${jira.url}", configuration = JiraConfig.class)
public interface JiraClient {

    @PostMapping("/issue")
    ResponseEntity<JiraIssueResponse> createIssue(@RequestBody JiraIssueRequest request);

    @PutMapping("/issue/{issueIdOrKey}")
    ResponseEntity<Void> updateIssue(
            @PathVariable("issueIdOrKey") String issueIdOrKey,
            @RequestBody JiraIssueRequest request
    );

    @GetMapping("/issue/{issueIdOrKey}")
    ResponseEntity<JiraIssueResponse> getIssue(@PathVariable("issueIdOrKey") String issueId);

    @GetMapping("/search")
    ResponseEntity<JiraSearchResponse> searchIssues(
            @RequestParam("jql") String jql,
            @RequestParam(value = "startAt", required = false, defaultValue = "0") Integer startAt,
            @RequestParam(value = "maxResults", required = false, defaultValue = "50") Integer maxResults,
            @RequestParam(value = "fields", required = false,
                    defaultValue = "summary,status,assignee,duedate,parent,issuelinks") String fields
    );
}