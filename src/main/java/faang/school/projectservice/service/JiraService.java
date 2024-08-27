package faang.school.projectservice.service;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.*;
import faang.school.projectservice.dto.issue.IssueDto;
import faang.school.projectservice.mapper.IssueMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JiraService {
    private final JiraRestClient jiraRestClient;
    private final IssueMapper issueMapper;

    public void createIssue(String projectKey, IssueDto issueDto) {
        IssueInput input = new IssueInputBuilder(projectKey, issueDto.getTypeId())
                .setSummary(issueDto.getSummary())
                .setDescription(issueDto.getDescription())
                .setDueDate(issueDto.getDueDate().toDateTime())
                .build();

        jiraRestClient.getIssueClient().createIssue(input).claim();
    }

    public IssueDto getIssueDto(String issueKey) {
        return issueMapper.toIssueDto(getIssue(issueKey));
    }

    public void updateIssueDescription(String issueKey, String description) {
        IssueInput input = new IssueInputBuilder()
                .setDescription(description)
                .build();

        updateIssue(issueKey, input);
    }

    public void updateIssueStatus(String issueKey, int statusId) {
        TransitionInput transitionInput = new TransitionInput(statusId);
        try {
            jiraRestClient.getIssueClient().transition(getIssue(issueKey), transitionInput);
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public void updateIssueParent(String issueKey, String parentKey) {
        IssueInput input = new IssueInputBuilder()
                .setFieldInput(new FieldInput("parent", ComplexIssueInputFieldValue.with("key", parentKey)))
                .build();

        jiraRestClient.getIssueClient().updateIssue(issueKey, input).claim();
    }

    public void deleteIssue(String issueKey, boolean deleteSubtask) {
        try {
            jiraRestClient.getIssueClient().deleteIssue(issueKey, deleteSubtask).claim();
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public Iterable<IssueDto> searchIssues(String jql) {
        try {
            return issueMapper.toIssueDtoIterable(
                    jiraRestClient.getSearchClient().searchJql(jql).claim().getIssues());
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public void addComment(String issueKey, String commentBody) {
        try {
            jiraRestClient.getIssueClient().addComment(
                    getIssue(issueKey).getCommentsUri(), Comment.valueOf(commentBody));
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private void updateIssue(String issueKey, IssueInput input) {
        try {
            jiraRestClient.getIssueClient().updateIssue(issueKey, input).claim();
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private Issue getIssue(String issueKey) {
        try {
            return jiraRestClient.getIssueClient().getIssue(issueKey).claim();
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}