package faang.school.projectservice.controller.jira;

import faang.school.projectservice.dto.jira.issue.request.JiraCreateIssueRequest;
import faang.school.projectservice.dto.jira.issue.request.JiraGetMultipleIssuesDto;
import faang.school.projectservice.dto.jira.issue.request.JiraUpdateIssueRequest;
import faang.school.projectservice.dto.jira.issue.response.JiraCreateIssueResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraGetIssueResponse;
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
    public JiraCreateIssueResponse createIssue(@RequestBody JiraCreateIssueRequest requestBody) {
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
        if (dto.getJiraIssueFilterDto() == null ||
                (dto.getJiraIssueFilterDto().getAssignee() == null &&
                        dto.getJiraIssueFilterDto().getStatus() == null)) {
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
    public JiraGetIssueResponse getIssueById(@PathVariable long issueId) {
        return jiraService.getIssueById(issueId);
    }


}
