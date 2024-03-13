package faang.school.projectservice.controller.jira;


import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.service.jira.JiraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/jira/issue")
@RequiredArgsConstructor
public class JiraController {

    private final JiraService jiraService;

    @PostMapping
    public ResponseEntity<String> createIssue(@RequestBody IssueDto issueDto) {
        return jiraService.createIssue(issueDto);
    }

    @GetMapping("/{issueIdOrKey}")
    public ResponseEntity<String> getIssue(@PathVariable String issueIdOrKey) {
        return jiraService.getIssue(issueIdOrKey);
    }

    @GetMapping("/filtered")
    public ResponseEntity<String> getIssuesByFilter(IssueFilterDto issueFilterDto) {
        return jiraService.getIssuesByFilter(issueFilterDto);
    }

    @GetMapping
    public ResponseEntity<String> getAllIssue() {
        return jiraService.getAllIssue();
    }


}
