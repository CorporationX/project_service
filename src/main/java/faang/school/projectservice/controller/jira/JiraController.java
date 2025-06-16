package faang.school.projectservice.controller.jira;

import faang.school.projectservice.dto.jira.issue.request.JiraCreateIssueDto;
import faang.school.projectservice.dto.jira.issue.request.JiraGetMultipleIssuesDto;
import faang.school.projectservice.dto.jira.issue.request.JiraUpdateIssueRequest;
import faang.school.projectservice.dto.jira.issue.response.JiraCreateIssueResponseDto;
import faang.school.projectservice.dto.jira.issue.response.JiraGetIssueResponseDto;
import faang.school.projectservice.dto.jira.issue.response.JiraGetMultipleIssuesResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraUpdateIssueResponse;
import faang.school.projectservice.service.JiraService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/jira")
@RequiredArgsConstructor
public class JiraController {

    private final JiraService jiraService;

    @PostMapping("/issues")
    public JiraCreateIssueResponseDto createIssue(@RequestBody JiraCreateIssueDto requestBody) {
        return jiraService.createIssue(requestBody);
    }

    @PutMapping("/issues/{issueId}")
    public JiraUpdateIssueResponse changeIssue(@PathVariable long issueId,
                                               @RequestBody JiraUpdateIssueRequest requestBody) {
        return jiraService.changeIssue(issueId, requestBody);
    }

    @GetMapping("/projects/{projectKey}/filter")
    public JiraGetMultipleIssuesResponse getAllIssuesWithFilter(@PathVariable String projectKey,
                                                                @RequestBody JiraGetMultipleIssuesDto dto) {
        if (dto.getJiraTaskFilterDto() == null ||
                (dto.getJiraTaskFilterDto().getAssignee() == null && dto.getJiraTaskFilterDto().getStatus() == null)) {
            return jiraService.getAllIssues(projectKey, dto);
        }

        return jiraService.getAllIssuesWithFilter(projectKey, dto);
    }

    @GetMapping("/projects/{projectKey}")
    public JiraGetMultipleIssuesResponse getAllIssues(@PathVariable String projectKey,
                                                      @RequestBody JiraGetMultipleIssuesDto dto) {
        return jiraService.getAllIssues(projectKey, dto);
    }

    @GetMapping("/issues/{issueId}")
    public JiraGetIssueResponseDto getIssueById(@PathVariable long issueId) {
        return jiraService.getIssueById(issueId);
    }


}
