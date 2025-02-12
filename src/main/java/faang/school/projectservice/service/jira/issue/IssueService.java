package faang.school.projectservice.service.jira.issue;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import faang.school.projectservice.client.jira.JiraClient;
import faang.school.projectservice.dto.jira.issue.IssueFilterDto;
import faang.school.projectservice.utils.parser.JqlParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class IssueService {
    private final JiraClient jiraClient;
    private final JqlParser jqlParser;

    public Issue createIssue(IssueInput issueInput) {
        log.info("Starting creation of issue with input: {}", issueInput);

        IssueRestClient issueClient = jiraClient.getIssueClient();
        BasicIssue basicIssue = issueClient.createIssue(issueInput).claim();

        log.info("Issue created successfully with key: {}", basicIssue.getKey());

        return getIssueByKey(basicIssue.getKey());
    }

    public Issue updateIssue(String issueKey, IssueInput issueInput) {
        log.info("Updating issue {} with input: {}", issueKey, issueInput);

        IssueRestClient issueClient = jiraClient.getIssueClient();
        issueClient.updateIssue(issueKey, issueInput).claim();

        log.info("Issue {} updated successfully", issueKey);

        return getIssueByKey(issueKey);
    }

    public List<Issue> getAllIssues(IssueFilterDto filters) {
        log.info("Fetching issues with filters: {}", filters);
        SearchRestClient searchClient = jiraClient.getSearchClient();

        String jql = jqlParser.buildJql(filters);
        SearchResult result = searchClient.searchJql(jql).claim();

        List<Issue> issues = StreamSupport.stream(result.getIssues().spliterator(), false).toList();

        log.debug("Founded issues {}", issues);

        return issues;
    }

    public Issue getIssueByKey(String issueKey) {
        log.info("Fetching issue by key: {}", issueKey);

        IssueRestClient issueClient = jiraClient.getIssueClient();
        Issue issue = issueClient.getIssue(issueKey).claim();

        log.debug("Retrieved issue {} details: {}", issueKey, issue);

        return issue;
    }
}
