package faang.school.projectservice.controller;

import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.dto.jira.IssueLinkCreationDto;
import faang.school.projectservice.dto.jira.IssueReadOnlyDto;
import faang.school.projectservice.dto.jira.IssueStatusUpdateDto;
import faang.school.projectservice.dto.jira.IssueFetchingDto;
import faang.school.projectservice.dto.jira.JiraProjectDto;
import faang.school.projectservice.service.JiraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jira/project")
@RequiredArgsConstructor
@Slf4j
@Validated
public class JiraController {

    private final JiraService jiraService;

    @PostMapping
    public JiraProjectDto saveProject(@Valid @RequestBody JiraProjectDto jiraProjectDto) {
        log.info("Received request to create project {}", jiraProjectDto);
        return jiraService.saveProject(jiraProjectDto);
    }

    @GetMapping("{projectKey}/issue/{issueKey}")
    public IssueReadOnlyDto getIssue(@PathVariable String projectKey, @PathVariable String issueKey) {
        log.info("Received request to get issue {} from project {}", issueKey, projectKey);
        return jiraService.getIssue(projectKey, issueKey);
    }

    @GetMapping("{projectKey}/issues")
    public IssueFetchingDto getIssues(@PathVariable String projectKey) {
        log.info("Received request to get all issues from project {}", projectKey);
        return jiraService.getIssues(projectKey);
    }

    @GetMapping("{projectKey}/issues/filter")
    public IssueFetchingDto getIssuesWithFilter(@PathVariable String projectKey, @RequestBody IssueFilterDto filter) {
        log.info("Received request to get all issues from project {} with filter {}", projectKey, filter);
        return jiraService.getIssuesWithFilter(projectKey, filter);
    }

    @PostMapping("{projectKey}/issue")
    public IssueReadOnlyDto createIssue(@PathVariable String projectKey, @Valid @RequestBody IssueDto issue) {
        log.info("Received request to create issue {} in project {}", issue, projectKey);
        return jiraService.createIssue(projectKey, issue);
    }

    @PutMapping("{projectKey}/issue/{issueKey}")
    public String updateIssue(@PathVariable String projectKey,
                              @PathVariable String issueKey,
                              @RequestBody IssueDto issue) {
        log.info("Received request to update issue {} in project {}", issue, projectKey);
        return jiraService.updateIssue(projectKey, issueKey, issue);
    }

    @PutMapping("{projectKey}/issue/{issueKey}/status/change")
    public String changeIssueStatus(@PathVariable String projectKey,
                                    @PathVariable String issueKey,
                                    @Valid @RequestBody IssueStatusUpdateDto issueStatus) {
        log.info("Received request to change issue status {} in project {}", issueKey, projectKey);
        return jiraService.changeIssueStatus(projectKey, issueKey, issueStatus);
    }

    @PostMapping("{projectKey}/issueLink")
    public String createIssueLink(@PathVariable String projectKey,
                                  @Valid @RequestBody IssueLinkCreationDto issueLinkCreationDto) {
        log.info("Received request to create issue link {} in project {}", issueLinkCreationDto, projectKey);
        return jiraService.createIssueLink(projectKey, issueLinkCreationDto);
    }
}
