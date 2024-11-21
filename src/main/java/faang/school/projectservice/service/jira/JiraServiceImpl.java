package faang.school.projectservice.service.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.filter.jirafilter.IssueFilter;
import faang.school.projectservice.mapper.IssueMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JiraServiceImpl implements JiraService {

    private final JiraRestClient jiraRestClient;
    private final IssueMapper mapper;
    private final List<IssueFilter> issueFilter;


    @Override
    public IssueDto createIssue(String projectKey, IssueDto issueDto) {
        try {
            IssueInput issueInput = new IssueInputBuilder(projectKey, issueDto.typeId())
                    .setSummary(issueDto.summary())
                    .setDescription(issueDto.description())
                    .setDueDate(issueDto.dueDate().toDateTime())
                    .setIssueType(mapper.toIssueType(issueDto.issueType()))
                    .build();
            BasicIssue basicIssue = jiraRestClient.getIssueClient().createIssue(issueInput).claim();
            log.info("The task was successfully created with the key: {}", basicIssue.getKey());
            Issue issue = jiraRestClient.getIssueClient().getIssue(basicIssue.getKey()).claim();
            return mapper.toIssueDto(issue);
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public List<IssueDto> getAllIssueByFilter(IssueFilterDto filter) {
        log.info("Fetching issues with filter: {}", filter);
        String jql = issueFilter.stream()
                .filter(filters -> filters.isApplicable(filter))
                .map(filters -> filters.getJql(filter))
                .collect(Collectors.joining(""));
        log.debug("Generated JQL query: {}", jql);
        try {
            SearchResult result = jiraRestClient.getSearchClient().searchJql(jql).claim();
            return mapper.toIssueDtos((List<Issue>) result.getIssues());
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public IssueDto getIssue(String issueKey) {
        log.info("Fetching issue with key: {}", issueKey);
        try {
            Issue issue = jiraRestClient.getIssueClient().getIssue(issueKey).claim();
            log.info("Successfully fetched issue: {}", issueKey);
            return mapper.toIssueDto(issue);
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}