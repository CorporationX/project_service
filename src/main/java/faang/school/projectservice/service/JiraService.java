package faang.school.projectservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.cache.JiraProjectCache;
import faang.school.projectservice.client.JiraClient;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.dto.jira.IssueReadOnlyDto;
import faang.school.projectservice.dto.jira.IssueLinkCreationDto;
import faang.school.projectservice.dto.jira.IssueStatusTransition;
import faang.school.projectservice.dto.jira.IssueStatusUpdateDto;
import faang.school.projectservice.dto.jira.IssueFetchingDto;
import faang.school.projectservice.dto.jira.JiraProjectDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.jira.IssueFilter;
import faang.school.projectservice.jpa.JiraProjectRepository;
import faang.school.projectservice.mapper.jira.IssueStatusMapper;
import faang.school.projectservice.mapper.jira.JiraProjectMapper;
import faang.school.projectservice.model.jira.JiraProject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class JiraService {

    private final JiraProjectRepository jiraProjectRepository;
    private final JiraProjectCache jiraProjectCache;
    private final JiraClient jiraClient;
    private final ObjectMapper objectMapper;
    private final JiraProjectMapper jiraProjectMapper;
    private final IssueStatusMapper issueStatusMapper;
    private final List<IssueFilter> filters;

    @Transactional
    public JiraProjectDto saveProject(JiraProjectDto jiraProjectDto) {
        JiraProject project = jiraProjectMapper.toEntity(jiraProjectDto);
        project = jiraProjectRepository.save(project);

        return jiraProjectMapper.toDto(project);
    }

    public IssueReadOnlyDto getIssue(String projectKey, String issueKey) {
        JiraProject project = getJiraProject(projectKey);
        String issueJson = jiraClient.getIssue(project, issueKey);

        return readValue(issueJson, IssueReadOnlyDto.class);
    }

    public IssueFetchingDto getIssues(String projectKey) {
        IssueFilterDto filter = IssueFilterDto.builder()
                .project(projectKey)
                .build();

        return getIssuesWithFilter(projectKey, filter);
    }

    public IssueFetchingDto getIssuesWithFilter(String projectKey, IssueFilterDto filter) {
        JiraProject project = getJiraProject(projectKey);

        StringBuilder filterBuilder = new StringBuilder();
        filters.stream()
                .filter(f -> f.isApplicable(filter))
                .forEach(f -> f.apply(filterBuilder, filter));

        String issueJson = jiraClient.getIssuesWithFilter(project, filterBuilder.toString());

        return readValue(issueJson, IssueFetchingDto.class);
    }

    public IssueReadOnlyDto createIssue(String projectKey, IssueDto issue) {
        JiraProject project = getJiraProject(projectKey);
        String body = writeValue(issue);
        String issueJson = jiraClient.createIssue(project, body);

        return readValue(issueJson, IssueReadOnlyDto.class);
    }

    public String updateIssue(String projectKey, String issueKey, IssueDto issue) {
        JiraProject project = getJiraProject(projectKey);

        if (Objects.isNull(issue.getFields().getSummary())) {
            IssueReadOnlyDto oldIssue = getIssue(projectKey, issueKey);
            issue.getFields().setSummary(oldIssue.getFields().getSummary());
        }

        String body = writeValue(issue);

        return jiraClient.updateIssue(project, issueKey, body);
    }

    public String changeIssueStatus(String projectKey, String issueKey, IssueStatusUpdateDto issueStatus) {
        JiraProject project = getJiraProject(projectKey);

        IssueStatusTransition transition = issueStatusMapper.toTransition(issueStatus);
        String body = writeValue(transition);

        return jiraClient.changeIssueStatus(project, issueKey, body);
    }

    public String createIssueLink(String projectKey, IssueLinkCreationDto issueLinkCreationDto) {
        JiraProject project = getJiraProject(projectKey);
        String body = writeValue(issueLinkCreationDto);

        return jiraClient.createIssueLink(project, body);
    }

    private JiraProject getJiraProject(String projectKey) {
        return jiraProjectCache.get(projectKey)
                .orElseThrow(() -> new EntityNotFoundException("Project with key " + projectKey + " not found"));
    }

    private String writeValue(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T readValue(String value, Class<T> clazz) {
        try {
            return objectMapper.readValue(value, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
