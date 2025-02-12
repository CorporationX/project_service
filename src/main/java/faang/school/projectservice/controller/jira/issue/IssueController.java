package faang.school.projectservice.controller.jira.issue;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import faang.school.projectservice.dto.jira.issue.IssueDto;
import faang.school.projectservice.dto.jira.issue.IssueFilterDto;
import faang.school.projectservice.mapper.jira.issue.IssueMapper;
import faang.school.projectservice.service.jira.issue.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/jira")
@RestController
public class IssueController {

    private final IssueService issueService;
    private final IssueMapper issueMapper;

    @PostMapping("/issue/{projectKey}")
    public ResponseEntity<IssueDto> createIssue(@PathVariable String projectKey, @Valid @RequestBody IssueDto requestIssueDto) {
        IssueInput requestIssueInput = issueMapper.toIssueInput(projectKey, requestIssueDto);
        Issue responseIssue = issueService.createIssue(requestIssueInput);
        IssueDto responseIssueDto = issueMapper.toDto(responseIssue);
        return ResponseEntity.ok(responseIssueDto);
    }

    @PutMapping("/issue/{issueKey}")
    public ResponseEntity<IssueDto> updateIssue(@PathVariable String issueKey, @Valid @RequestBody IssueDto requestIssueDto) {
        IssueInput requestIssueInput = issueMapper.toIssueInput(requestIssueDto);
        Issue responseIssue = issueService.updateIssue(issueKey, requestIssueInput);
        IssueDto responseIssueDto = issueMapper.toDto(responseIssue);
        return ResponseEntity.ok(responseIssueDto);
    }

    @PostMapping("/issue")
    public ResponseEntity<List<IssueDto>> getAllIssues(@RequestBody(required = false) IssueFilterDto filters) {
        List<Issue> responseIssues = issueService.getAllIssues(filters);
        List<IssueDto> responseIssuesDto = issueMapper.toDto(responseIssues);
        return ResponseEntity.ok(responseIssuesDto);
    }

    @GetMapping("/issue/{issueKey}")
    public ResponseEntity<IssueDto> getIssueByKey(String issueKey) {
        Issue responseIssue = issueService.getIssueByKey(issueKey);
        IssueDto responseIssueDto = issueMapper.toDto(responseIssue);
        return ResponseEntity.ok(responseIssueDto);
    }
}
