package faang.school.projectservice.service.jira;

import faang.school.projectservice.client.JiraClient;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.dto.jira.IssueUpdateDto;
import faang.school.projectservice.dto.jira.JiraResponse;
import faang.school.projectservice.filter.IssueFilter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.JiraService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraServiceImpl implements JiraService {

    private final JiraClient jiraClient;
    private final List<IssueFilter> issueFilters;
    private final ProjectJpaRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectDto registrationInJira(long id, String jiraKey) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project with id %d not found".formatted(id)));
        project.setJiraKey(jiraKey);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public List<IssueDto> getAllIssuesByProjectId(long projectId) {
        String jiraProjectKey = getJiraProjectKey(projectId);
        String jql = "project = '%s'".formatted(jiraProjectKey);
        JiraResponse response = jiraClient.getProjectInfoByJql(jql);

        return Optional.ofNullable(response)
                .map(JiraResponse::getIssues)
                .orElse(Collections.emptyList());
    }

    @Override
    public IssueDto getIssueByKey(String issueKey) {
        return jiraClient.getIssueByKey(issueKey);
    }

    @Override
    public List<IssueDto> getAllIssuesWithFilterByProjectId(long projectKey, IssueFilterDto filterDto) {
        String jiraProjectKey = getJiraProjectKey(projectKey);
        String jql = issueFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .map(filter -> filter.createJql(filterDto))
                .collect(Collectors.joining(" AND "));
        jql += " AND project = '%s'".formatted(jiraProjectKey);

        return Optional.ofNullable(jiraClient.getProjectInfoByJql(jql))
                .map(JiraResponse::getIssues)
                .orElse(Collections.emptyList());
    }

    @Override
    public IssueDto createIssue(IssueDto issueDto) {
        return jiraClient.createIssue(issueDto);
    }

    @Override
    public void updateIssueByKey(String issueKey, IssueUpdateDto issueDto) {
        IssueUpdateDto.Fields fields = issueDto.getFields();

        if (fields != null && fields.getIssueLinks() != null) {
            jiraClient.createIssueLinks(fields.getIssueLinks());
            fields.setIssueLinks(null);
        }

        if (issueDto.getTransition() != null) {
            jiraClient.setTransitionByKey(issueKey, issueDto.getTransition());
            issueDto.setTransition(null);
        }

        jiraClient.updateIssueByKey(issueKey, issueDto);
    }

    private String getJiraProjectKey(long projectId) {
        return projectRepository.findById(projectId)
                .map(Project::getJiraKey)
                .orElseThrow(() -> new EntityNotFoundException("Проект с id %d не связан с Jira".formatted(projectId)));
    }
}
