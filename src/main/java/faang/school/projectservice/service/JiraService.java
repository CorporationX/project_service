package faang.school.projectservice.service;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import faang.school.projectservice.dto.issue.IssueDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.IssueMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraService {
    private final JiraRestClient jiraRestClient;
    private final IssueMapper issueMapper;

    @Value("${jira.max-results}")
    private int maxResults;

    public String createIssue(String projectKey, IssueDto issueDto) {
        IssueInput issue = new IssueInputBuilder(
                projectKey, issueDto.getTypeId(), issueDto.getSummary())
                .build();
        return jiraRestClient
                .getIssueClient()
                .createIssue(issue)
                .claim()
                .getKey();
    }

    public void updateIssueDescription(String issueKey, String description) {
        IssueInput input = new IssueInputBuilder()
                .setDescription(description)
                .build();
        jiraRestClient
                .getIssueClient()
                .updateIssue(issueKey, input)
                .claim();
    }

    public void updateIssueStatus(String issueKey, int statusId) {
        TransitionInput input = new TransitionInput(statusId);
        jiraRestClient
                .getIssueClient()
                .transition(getIssue(issueKey), input);
    }

    public void updateParentIssue(String issueKey, String parentKey) {
        IssueInput input = new IssueInputBuilder()
                .setFieldInput(new FieldInput("parent", ComplexIssueInputFieldValue.with("key", parentKey)))
                .build();
        jiraRestClient
                .getIssueClient()
                .updateIssue(issueKey, input);
    }

    public IssueDto getIssueDto(String issueKey) {
        return issueMapper.toIssueDto(getIssue(issueKey));
    }

    public Iterable<IssueDto> getAllIssue(String projectKey) {
        String jqlQuery = "project = \"" + projectKey + "\"";
        return getIssueWithFilter(jqlQuery);
    }

    public Iterable<IssueDto> getIssueWithFilter(String jqlFilter) {
        Set<String> fields = Set.of("summary", "assignee", "status", "dueDate", "comments");
        List<IssueDto> issueDto = new ArrayList<>();

        Iterable<Issue> result = jiraRestClient
                .getSearchClient()
                .searchJql(jqlFilter, maxResults, 0, fields)
                .claim().getIssues();
        issueMapper.toIterableIssueDto(result).forEach(issueDto::add);
        return issueDto;
    }

    public void addComment(String issueKey, String commentBody) {
        Issue issue = getIssue(issueKey);
        jiraRestClient.getIssueClient()
                .addComment(issue.getVotesUri(), Comment.valueOf(commentBody));
    }

    public List<Comment> getAllComments(String issueKey) {
        return StreamSupport.stream(getIssue(issueKey)
                        .getComments()
                        .spliterator(), false)
                .toList();
    }

    public void deleteIssue(String issueKey, boolean deleteSubtasks) {
        jiraRestClient.getIssueClient()
                .deleteIssue(issueKey, deleteSubtasks)
                .claim();
    }

    private Issue getIssue(String issueKey) {
        try {
            return jiraRestClient
                    .getIssueClient()
                    .getIssue(issueKey)
                    .claim();
        } catch (EntityNotFoundException e) {
            log.error("Ошибка поиска задачи", e);
            throw e;
        }
    }
}
