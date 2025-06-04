package faang.school.projectservice.controller.jira;

import faang.school.projectservice.dto.jira.task.JiraCreateIssueJSON;
import faang.school.projectservice.dto.jira.task.JiraCreateTaskDto;
import faang.school.projectservice.dto.jira.task.JiraChangeTaskDto;
import faang.school.projectservice.dto.jira.task.JiraTaskFilterDto;
import faang.school.projectservice.service.JiraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/jira")
@RequiredArgsConstructor
public class JiraController {

    private final JiraService jiraService;

    @PostMapping("/issues")
    public ResponseEntity<?> createIssue(@RequestBody JiraCreateTaskDto jiraCreateTaskDto) {
        Map<String, Object> result = jiraService.createIssue(jiraCreateTaskDto);
        return ResponseEntity.ok(result);
    }

    // Tак ведь лучше?
    // Если прикинуть с фронта мы бы получали запрос либо через открывающуюся вкладку где выбирали параметры таски перед созданием.
    // Или уже созданную таску с нашего сервиса, разбивали бы на необходимые компоненты и их отсылали бы?
    @PostMapping("/issues/json")
    public ResponseEntity<?> createIssueWithJSON(@RequestBody Map json) {
        Map<String, Object> result = jiraService.createIssueWithJSON(json);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/issues/{issueId}")
    public ResponseEntity<?> changeIssue(@PathVariable long issueId, @RequestBody JiraChangeTaskDto jiraTaskDto) {
        Map<String, Object> result = jiraService.changeIssue(issueId, jiraTaskDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/projects/{projectKey}/filter")
    public ResponseEntity<?> getAllIssuesWithFilter(@PathVariable String projectKey, JiraTaskFilterDto jiraTaskFilterDto) {
        if (jiraTaskFilterDto == null) {
            return getAllIssues(projectKey);
        }

        Map<String, Object> result = jiraService.getAllIssuesWithFilter(projectKey, jiraTaskFilterDto);
        return ResponseEntity.ok(result);
    }

    // Not enough permission, спросить у Михаила
    @GetMapping("/projects/{projectKey}")
    public ResponseEntity<?> getAllIssues(@PathVariable String projectKey) {
        Map<String, Object> result = jiraService.getAllIssues(projectKey);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/issues/{issueId}")
    public ResponseEntity<?> getIssueById(@PathVariable long issueId) {
        Map<String, Object> result = jiraService.getIssueById(issueId);
        return ResponseEntity.ok(result);
    }


}
