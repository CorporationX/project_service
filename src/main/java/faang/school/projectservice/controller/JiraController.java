package faang.school.projectservice.controller;

import faang.school.projectservice.dto.jira.filter.IssueFilterDto;
import faang.school.projectservice.dto.jira.request.IssueRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.dto.jira.response.IssuesResponseDto;
import faang.school.projectservice.dto.jira.response.ProjectResponseDto;
import faang.school.projectservice.dto.jira.update.IssueUpdateDto;
import faang.school.projectservice.service.jira.JiraService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.NonNegative;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/jira")
public class JiraController {
    private final JiraService jiraService;

    @PostMapping("/issue")
    public IssueCreateResponseDto createIssue(@RequestBody @NotNull IssueRequestDto issueRequestDto) {
        log.info("Creating issue started");
        IssueCreateResponseDto response = jiraService.createIssue(issueRequestDto);
        log.info("Creating issue completed");
        return response;
    }

    @PutMapping("/issue/update/{key}")
    public void updateIssue(@PathVariable @NotNull @NotBlank String key,
                            @RequestBody @NotNull IssueUpdateDto issueUpdateDto) {

        log.info("Updating issue with key {} started", key);
        jiraService.updateIssue(key, issueUpdateDto);
        log.info("Updating issue with key {} completed", key);
    }

    @GetMapping("/issue/project/{projectId}")
    public List<IssueResponseDto> getAllIssuesWithFilter(@PathVariable @NotNull @NonNegative Long projectId,
                                                          @RequestBody @NotNull IssueFilterDto issueFilterDto) {

        log.info("Getting all issues with filter for project {} started", projectId);
        List<IssueResponseDto> response = jiraService.getAllIssuesWithFilter(projectId, issueFilterDto);
        log.info("Getting all issues with filter for project {} completed, found {} issues", projectId, response.size());
        return response;
    }

    @GetMapping("/issues/project/{projectId}")
    public List<IssueResponseDto> getAllIssuesByProjectId(@PathVariable @NotNull @NonNegative Long projectId) {
        log.info("Getting all issues for project {} started", projectId);
        List<IssueResponseDto> response = jiraService.getAllIssuesByProject(projectId);
        log.info("Getting all issues for project {} completed, found {} issues", projectId, response.size());
        return response;
    }

    @GetMapping("/issues/{key}")
    public IssueResponseDto getIssueByKey(@PathVariable @NotNull @NotBlank String key) {
        log.info("Getting issue by key {} started", key);
        IssueResponseDto response = jiraService.getIssueByKey(key);
        log.info("Getting issue by key {} completed", key);
        return response;
    }

    @PostMapping("/project/{id}/key/{key}")
    public ProjectResponseDto registerProject(@PathVariable @NotNull @NonNegative Long id,
                                              @PathVariable @NotNull @NotBlank String key) {

        log.info("Registering project with id {} and key {} started", id, key);
        ProjectResponseDto response = jiraService.registerProject(id, key);
        log.info("Registering project with id {} and key {} completed", id, key);
        return response;
    }
}
