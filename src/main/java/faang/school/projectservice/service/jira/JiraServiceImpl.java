package faang.school.projectservice.service.jira;

import faang.school.projectservice.client.JiraClient;
import faang.school.projectservice.dto.jira.filter.IssueFilterDto;
import faang.school.projectservice.dto.jira.request.IssueRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.dto.jira.response.IssuesResponseDto;
import faang.school.projectservice.dto.jira.response.ProjectResponseDto;
import faang.school.projectservice.dto.jira.update.IssueUpdateDto;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.filter.jira.IssueFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.validation.JiraValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JiraServiceImpl implements JiraService {
    public static final String NO_APPLICABLE_FILTERS_SET = "No applicable filters set";
    public static final String PROJECT_DOES_NOT_CONNECTED_TO_JIRA = "Project with id %d does not connected to jira";

    private final JiraClient jiraClient;
    private final ProjectRepository projectRepository;
    private final List<IssueFilter> issueFilters;
    private final ProjectMapper projectMapper;

    @Override
    public Mono<IssueCreateResponseDto> createIssue(IssueRequestDto issueRequestDto) {
        JiraValidation.validateCreateIssue(issueRequestDto);
        log.info("Creating issue using JiraClient");
        return jiraClient.createIssue(issueRequestDto);
    }

    @Override
    public Mono<Void> updateIssue(String key, IssueUpdateDto issueUpdateDto) {
        JiraValidation.validateIssueKey(key);
        JiraValidation.validateIssueUpdateDto(issueUpdateDto);

        Mono<Void> updateLinksMono = Mono.empty();
        if (issueUpdateDto.getFields().getIssueLinks() != null) {
            log.info("Creating issue link using JiraClient");
            updateLinksMono = jiraClient.createIssueLinks(issueUpdateDto.getFields().getIssueLinks());
        }

        Mono<Void> transitionMono = Mono.empty();
        if (issueUpdateDto.getTransition() != null) {
            log.info("Setting transition using JiraClient");
            transitionMono = jiraClient.setTransitionByKey(key, issueUpdateDto.getTransition());
        }

        issueUpdateDto.getFields().setIssueLinks(null);

        return updateLinksMono
                .then(transitionMono)
                .then(Mono.defer(() -> {
                    log.info("Updating issue using JiraClient");
                    return jiraClient.updateIssueByKey(key, issueUpdateDto);
                }));
    }

    @Override
    public Flux<IssueResponseDto> getAllIssuesWithFilter(Long projectId, IssueFilterDto issueFilterDto) {
        return getProjectKey(projectId)
                .map(projectKey -> {
                    String query = issueFilters.stream()
                            .filter(issueFilter -> issueFilter.isApplicable(issueFilterDto))
                            .map(issueFilter -> issueFilter.createJql(issueFilterDto))
                            .collect(Collectors.joining(" AND "));

                    query += " AND project = " + projectKey;
                    if (query.charAt(0) == ' ') {
                        log.error("No applicable filters set");
                        throw new IllegalArgumentException("No applicable filters set");
                    }

                    log.info("Getting issues with filter using JiraClient");
                    return query;
                })
                .flatMapMany(jiraClient::getInfoByJql)
                .map(response -> Optional.ofNullable(response.getIssues()).orElse(Collections.emptyList()))
                .flatMap(Flux::fromIterable);
    }

    @Override
    public Flux<IssueResponseDto> getAllIssuesByProject(Long projectId) {
        return getProjectKey(projectId)
                .map(projectKey -> "project = " + projectKey)
                .flatMapMany(jiraClient::getInfoByJql)
                .map(IssuesResponseDto::getIssues)
                .flatMap(Flux::fromIterable);
    }

    @Override
    public Mono<IssueResponseDto> getIssueByKey(String key) {
        JiraValidation.validateIssueKey(key);
        log.info("Getting issue by key using JiraClient");
        return jiraClient.getIssueByKey(key);
    }

    @Override
    @Transactional
    public Mono<ProjectResponseDto> registerProject(Long id, String key) {
        JiraValidation.validateProjectKey(key);
        return Mono.fromCallable(() -> projectRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalProject -> optionalProject
                        .map(project -> {
                            project.setJiraKey(key);
                            return Mono.just(projectMapper.toProjectResponseDto(project));
                        })
                        .orElseGet(() -> Mono.error(new ProjectNotFoundException(
                                "Project with id %d not found".formatted(id)))));
    }

    private Mono<String> getProjectKey(Long projectId) {
        return Mono.fromCallable(() -> projectRepository.findById(projectId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalProject -> optionalProject
                        .map(project -> Mono.just(project.getJiraKey()))
                        .orElseGet(() -> Mono.error(new ProjectNotFoundException(
                                PROJECT_DOES_NOT_CONNECTED_TO_JIRA.formatted(projectId)))));
    }
}
