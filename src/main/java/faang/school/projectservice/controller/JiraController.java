package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.dto.jira.IssueUpdateDto;
import faang.school.projectservice.service.JiraService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/jira")
public class JiraController {
    private final JiraService jiraService;

    @PostMapping("/project/{id}/jira-key/{key}")
    public ProjectDto registrationProjectInJira(@PathVariable @Positive Long id, @PathVariable @NotNull String key) {
        return jiraService.registrationInJira(id, key);
    }

    @PostMapping("/issue")
    @ResponseStatus(HttpStatus.CREATED)
    public IssueDto createIssue(@RequestBody IssueDto issueDto) {
        return jiraService.createIssue(issueDto);
    }

    @GetMapping("/issue/project/{projectId}")
    public List<IssueDto> getAllIssuesByProjectId(@PathVariable @NotNull @Positive Long projectId) {
        return jiraService.getAllIssuesByProjectId(projectId);
    }

    @GetMapping("/issue/filter/project/{projectId}")
    public List<IssueDto> getAllIssuesWithFilterByProjectKey(
            @PathVariable @NotNull @Positive Long projectId,
            @RequestBody IssueFilterDto filter) {
        return jiraService.getAllIssuesWithFilterByProjectId(projectId, filter);
    }

    @GetMapping("/issue/{key}")
    public IssueDto getIssueByKey(@PathVariable @NotNull @NotBlank String key) {
        return jiraService.getIssueByKey(key);
    }

    @PutMapping("/issue/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateIssue(@PathVariable @NotNull @NotBlank String key, @RequestBody IssueUpdateDto issueDto) {
        jiraService.updateIssueByKey(key, issueDto);
    }
}
