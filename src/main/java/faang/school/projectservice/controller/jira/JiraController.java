package faang.school.projectservice.controller.jira;

import faang.school.projectservice.dto.jira.task.JiraIssueFilterDto;
import faang.school.projectservice.service.JiraService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/v1/jira")
@RequiredArgsConstructor
public class JiraController {

    private final JiraService jiraService;

    @PostMapping("/issues")
    public ResponseEntity<?> createIssue(@RequestBody Map body) {
        Map<String, Object> result = jiraService.createIssue(body);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/issues/{issueId}")
    public ResponseEntity<?> changeIssue(@PathVariable long issueId, @RequestBody Map body) {
        Map<String, Object> result = jiraService.changeIssue(issueId, body);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/projects/{projectKey}/filter")
    public ResponseEntity<?> getAllIssuesWithFilter(@PathVariable String projectKey,
                                                    @RequestBody JiraIssueFilterDto jiraTaskFilterDto,
                                                    @RequestParam(defaultValue = "0") int startAt,
                                                    @RequestParam(defaultValue = "100") int maxResults,
                                                    @RequestParam(required = false) Integer limit) {
        if (jiraTaskFilterDto == null ||
                (jiraTaskFilterDto.getAssignee() == null && jiraTaskFilterDto.getStatus() == null)) {
            return getAllIssues(projectKey, startAt, maxResults, limit);
        }

        Map<String, Object> result = jiraService.getAllIssuesWithFilter(projectKey, jiraTaskFilterDto, startAt, maxResults, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/projects/{projectKey}")
    public ResponseEntity<?> getAllIssues(@PathVariable String projectKey,
                                          @RequestParam(defaultValue = "0") int startAt,
                                          @RequestParam(defaultValue = "100") int maxResults,
                                          @RequestParam(required = false) Integer limit) {
        Map<String, Object> result = jiraService.getAllIssues(projectKey, startAt, maxResults, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/issues/{issueId}")
    public ResponseEntity<?> getIssueById(@PathVariable long issueId) {
        Map<String, Object> result = jiraService.getIssueById(issueId);
        return ResponseEntity.ok(result);
    }


}
