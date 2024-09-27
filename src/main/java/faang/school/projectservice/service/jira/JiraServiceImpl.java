package faang.school.projectservice.service.jira;

import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.dto.jira.IssueUpdateDto;
import faang.school.projectservice.dto.jira.JiraResponse;
import faang.school.projectservice.exception.EntityFieldNotFoundException;
import faang.school.projectservice.filter.IssueFilter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.JiraService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraServiceImpl implements JiraService {

    private final WebClient webClient;
    private final List<IssueFilter> issueFilters;
    private final ProjectJpaRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectDto registrationProjectInJira(long id, String jiraKey) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project with id %d not found".formatted(id)));
        project.setJiraKey(jiraKey);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public List<IssueDto> getAllIssuesByProjectId(long projectId) {
        String jiraProjectKey = getJiraProjectKey(projectId);
        String jql = "project = \"%s\"".formatted(jiraProjectKey);
        JiraResponse response = fetchWithJql(jql);

        return Optional.ofNullable(response)
                .map(JiraResponse::getIssues)
                .orElse(Collections.emptyList());
    }

    @Override
    public IssueDto getIssueByKey(String issueKey) {
        return webClient.get()
                .uri("/issue/{key}", issueKey)
                .retrieve()
                .bodyToMono(IssueDto.class)
                .block();
    }

    @Override
    public List<IssueDto> getAllIssuesWithFilterByProjectId(long projectKey, IssueFilterDto filterDto) {
        String jiraProjectKey = getJiraProjectKey(projectKey);
        String jql = issueFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .map(filter -> filter.createJql(filterDto))
                .collect(Collectors.joining(" AND "));
        jql += " AND project = '%s'".formatted(jiraProjectKey);

        return Optional.ofNullable(fetchWithJql(jql))
                .map(JiraResponse::getIssues)
                .orElse(Collections.emptyList());
    }

    @Override
    public IssueDto createIssue(IssueDto issueDto) {
        return webClient.post()
                .uri("/issue")
                .bodyValue(issueDto)
                .retrieve()
                .bodyToMono(IssueDto.class)
                .block();
    }

    @Override
    public void updateIssueByKey(String issueKey, IssueUpdateDto issueDto) {
        IssueUpdateDto.Fields fields = issueDto.getFields();

        if (fields != null && fields.getIssueLinks() != null) {
            fields.getIssueLinks().forEach(issueLink -> webClient.post()
                    .uri("/issueLink")
                    .bodyValue(issueLink)
                    .retrieve()
                    .toBodilessEntity()
                    .block()
            );
            fields.setIssueLinks(null);
        }

        if (issueDto.getTransition() != null) {
            webClient.post()
                    .uri("/issue/{issueKey}/transitions", issueKey)
                    .bodyValue(Map.of("transition", issueDto.getTransition()))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            issueDto.setTransition(null);
        }

        webClient.put()
                .uri("/issue/{key}", issueKey)
                .bodyValue(issueDto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private JiraResponse fetchWithJql(String jql) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("jql", jql)
                        .build())
                .retrieve()
                .bodyToMono(JiraResponse.class)
                .block();
    }

    private String getJiraProjectKey(long projectId) {
        return projectRepository.findById(projectId)
                .map(Project::getJiraKey)
                .orElseThrow(() -> new EntityFieldNotFoundException("Данный проект не связан с Jira"));
    }
}
