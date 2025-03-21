package faang.school.projectservice.controller;

import faang.school.projectservice.dto.jira.filter.IssueFilterDto;
import faang.school.projectservice.dto.jira.request.IssueRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
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
        log.info("start {}", issueRequestDto.getFields().getIssuetype());
        return jiraService.createIssue(issueRequestDto);
    }//

    @PutMapping("/issue/update/{key}")
    public void updateIssue(@PathVariable @NotNull @NotBlank String key,
                            @RequestBody @NotNull IssueUpdateDto issueUpdateDto) {
        jiraService.updateIssue(key, issueUpdateDto);
    }

    @GetMapping("/issue/filter/project/{projectKey}")
    public List<IssueRequestDto> getAllIssuesWithFilter(@PathVariable @NotNull @NonNegative Long projectId,
                                                        @RequestBody @NotNull IssueFilterDto issueFilterDto) {
        return jiraService.getAllIssuesWithFilter(projectId, issueFilterDto);
    }

    @GetMapping("/issues/project/{projectId}")
    public List<IssueRequestDto> getAllIssuesByProjectId(@PathVariable @NotNull @NonNegative Long projectId) {
        return jiraService.getAllIssuesByProject(projectId);
    }

    @GetMapping("/issues/{key}")
    public IssueRequestDto getIssueByKey(@PathVariable @NotNull @NotBlank String key) {
        return jiraService.getIssueByKey(key);
    }//

    @PostMapping("/project/{id}/key/{key}")
    public ProjectResponseDto registerProject(@PathVariable @NotNull @NonNegative Long id,
                                              @PathVariable @NotNull @NotBlank String key) {
        return jiraService.registerProject(id, key);
    }//
}
