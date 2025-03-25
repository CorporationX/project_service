package faang.school.projectservice.service.jira;

import faang.school.projectservice.client.JiraClient;
import faang.school.projectservice.dto.jira.filter.IssueFilterDto;
import faang.school.projectservice.dto.jira.request.IssueRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.dto.jira.response.IssuesResponseDto;
import faang.school.projectservice.dto.jira.response.ProjectResponseDto;
import faang.school.projectservice.dto.jira.update.IssueUpdateDto;
import faang.school.projectservice.filter.jira.IssueFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JiraServiceImpl implements JiraService {
    private final JiraClient jiraClient;
    private final ProjectRepository projectRepository;
    private final List<IssueFilter> issueFilters;
    private final ProjectMapper projectMapper;

    @Override
    public IssueCreateResponseDto createIssue(IssueRequestDto issueRequestDto) {
        log.info("Creating issue using JiraClient");
        return jiraClient.createIssue(issueRequestDto);
    }

    @Override
    public void updateIssue(String key, IssueUpdateDto issueUpdateDto) {
        log.info("Creating issue link using JiraClient");
        jiraClient.createIssueLinks(issueUpdateDto.getFields().getIssueLinks());
        log.info("Setting transition using JiraClient");
        jiraClient.setTransitionByKey(key, issueUpdateDto.getFields().getDescription());

        jiraClient.updateIssueByKey(key, issueUpdateDto);
    }

    @Override
    public List<IssueResponseDto> getAllIssuesWithFilter(Long projectId, IssueFilterDto issueFilterDto) {
        String projectKey = getProjectKey(projectId);
        String jql = issueFilters.stream()
                .filter(issueFilter -> issueFilter.isApplicable(issueFilterDto))
                .map(issueFilter -> issueFilter.createJql(issueFilterDto))
                .collect(Collectors.joining(" AND "));
        jql += " AND project = " + projectKey;
        System.out.println(jql);
        return jiraClient.getInfoByJql(jql).getIssues();
    }

    @Override
    public List<IssueResponseDto> getAllIssuesByProject(Long projectId) {
        String projectKey = getProjectKey(projectId);
        String jql = "project = " + projectKey;
        return Optional.ofNullable(jiraClient.getInfoByJql(jql))
                .map(IssuesResponseDto::getIssues)
                .orElse(Collections.emptyList());
    }

    @Override
    public IssueResponseDto getIssueByKey(String key) {
        log.info("Getting issue by key using JiraClient");
        return jiraClient.getIssueByKey(key);
    }

    @Override
    @Transactional
    public ProjectResponseDto registerProject(Long id, String key) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Project with id %d not found".formatted(id)));
        project.setJiraKey(key);
        return projectMapper.toProjectResponseDto(project);
    }

    private String getProjectKey(Long projectId) {
        return projectRepository.findById(projectId)
                .map(Project::getJiraKey)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Project with id %d not connected to jira".formatted(projectId)));
    }
}
