package faang.school.projectservice.service.jira;

import faang.school.projectservice.client.jira.JiraRestClient;
import faang.school.projectservice.dto.jira.issue.request.JiraCreateIssueDto;
import faang.school.projectservice.dto.jira.issue.request.JiraGetMultipleIssuesDto;
import faang.school.projectservice.dto.jira.issue.request.JiraUpdateIssueRequest;
import faang.school.projectservice.dto.jira.issue.response.JiraCreateIssueResponseDto;
import faang.school.projectservice.dto.jira.issue.response.JiraGetIssueResponseDto;
import faang.school.projectservice.dto.jira.issue.response.JiraGetMultipleIssuesResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraUpdateIssueResponse;
import faang.school.projectservice.service.JiraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraServiceImpl implements JiraService {

    private final JiraRestClient jiraRestClient;

    @Override
    public JiraCreateIssueResponseDto createIssue(JiraCreateIssueDto jiraCreateIssueDto) {
        try {
            ResponseEntity<JiraCreateIssueResponseDto> response = jiraRestClient.createIssue(jiraCreateIssueDto);
            logSuccessfulResponse(response);

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public JiraUpdateIssueResponse changeIssue(long issueId, JiraUpdateIssueRequest requestBody) {
        try {
            ResponseEntity<JiraUpdateIssueResponse> response = jiraRestClient.changeIssue(issueId, requestBody);
            logSuccessfulResponse(response);

            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                log.info("Task {} updated successfully and full issue data returned. Status: {}",
                        issueId,
                        response.getStatusCode());

                return response.getBody();
            } else {
                log.warn("Task {} updated, but no body or unexpected status: {}", issueId, response.getStatusCode());
                return new JiraUpdateIssueResponse();
            }
        } catch (HttpClientErrorException e) {
            log.error("Error updating task {}. Response Body: {}", issueId, e.getResponseBodyAsString(), e);
            throw e;
        }
    }

    @Override
    public JiraGetMultipleIssuesResponse getAllIssuesWithFilter(String projectKey,
                                                                JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto) {
        try {
            ResponseEntity<JiraGetMultipleIssuesResponse> response = jiraRestClient.getAllIssuesWithFilter(
                    projectKey,
                    jiraGetMultipleIssuesDto
            );
            logSuccessfulResponse(response);

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public JiraGetMultipleIssuesResponse getAllIssues(String projectKey,
                                                      JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto) {
        try {
            ResponseEntity<JiraGetMultipleIssuesResponse> response = jiraRestClient.getAllIssues(
                    projectKey,
                    jiraGetMultipleIssuesDto
            );
            logSuccessfulResponse(response);

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public JiraGetIssueResponseDto getIssueById(long issueId) {
        try {
            ResponseEntity<JiraGetIssueResponseDto> response = jiraRestClient.getIssueById(issueId);
            logSuccessfulResponse(response);

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    private void logSuccessfulResponse(ResponseEntity<?> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Successful response with ID: {}", response.getStatusCode());
            log.info("Response body: {}", response.getBody());
        }
    }
}