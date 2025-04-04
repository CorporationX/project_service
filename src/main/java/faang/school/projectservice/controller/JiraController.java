package faang.school.projectservice.controller;

import faang.school.projectservice.dto.jira.filter.IssueFilterDto;
import faang.school.projectservice.dto.jira.request.IssueRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/jira")
public class JiraController {
    private final JiraService jiraService;

    @PostMapping("/issue")
    public Mono<IssueCreateResponseDto> createIssue(@RequestBody @NotNull IssueRequestDto issueRequestDto) {
        log.info("Creating issue started");
        return jiraService.createIssue(issueRequestDto)
                .doOnTerminate(() -> log.info("Creating issue completed"));
    }

    @PutMapping("/issue/update/{key}")
    public Mono<Void> updateIssue(@PathVariable @NotNull @NotBlank String key,
                                  @RequestBody @NotNull IssueUpdateDto issueUpdateDto) {

        log.info("Updating issue with key {} started", key);
        return jiraService.updateIssue(key, issueUpdateDto)
                .doOnTerminate(() -> log.info("Updating issue with key {} completed", key));
    }

    @GetMapping("/issue/project/{projectId}")
    public Flux<IssueResponseDto> getAllIssuesWithFilter(@PathVariable @NotNull @NonNegative Long projectId,
                                                         @RequestBody @NotNull IssueFilterDto issueFilterDto) {

        log.info("Getting all issues with filter for project {} started", projectId);
        return jiraService.getAllIssuesWithFilter(projectId, issueFilterDto)
                .doOnTerminate(() -> log.info("Getting all issues with filter for project" +
                        " {} completed", projectId));
    }

    @GetMapping("/issues/project/{projectId}")
    public Flux<IssueResponseDto> getAllIssuesByProjectId(@PathVariable @NotNull @NonNegative Long projectId) {
        log.info("Getting all issues for project {} started", projectId);
        return jiraService.getAllIssuesByProject(projectId)
                .doOnTerminate(() -> log.info("Getting all issues for project {} completed", projectId));
    }

    @GetMapping("/issues/{key}")
    public Mono<IssueResponseDto> getIssueByKey(@PathVariable @NotNull @NotBlank String key) {
        log.info("Getting issue by key {} started", key);
        return jiraService.getIssueByKey(key)
                .doOnTerminate(() -> log.info("Getting issue by key {} completed", key));
    }

    @PostMapping("/project/{id}/key/{key}")
    public Mono<ProjectResponseDto> registerProject(@PathVariable @NotNull @NonNegative Long id,
                                                    @PathVariable @NotNull @NotBlank String key) {

        log.info("Registering project with id {} and key {} started", id, key);
        return jiraService.registerProject(id, key)
                .doOnTerminate(() -> log.info("Registering project with id {} and key {} completed", id, key));
    }
}
