package school.faang.project_service.service;

import faang.school.projectservice.client.JiraClient;
import faang.school.projectservice.dto.jira.request.JiraIssueRequest;
import faang.school.projectservice.dto.jira.response.JiraIssueResponse;
import faang.school.projectservice.dto.jira.response.JiraSearchResponse;
import faang.school.projectservice.gateway.impl.JiraClientGatewayImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JiraClientGatewayTest {
    public static final String TEST_ISSUE_ID = "TEST-123";

    @Mock
    private JiraClient jiraClient;

    @InjectMocks
    private JiraClientGatewayImpl jiraClientGateway;

    private JiraIssueResponse expectedResponse;

    @BeforeEach
    void setUp() {
        expectedResponse = new JiraIssueResponse("12345", TEST_ISSUE_ID, "http://jira/test-125");
    }

    @Test
    void createIssue_ReturnsIssueResponse() {
        JiraIssueRequest request = new JiraIssueRequest();
        when(jiraClient.createIssue(request)).thenReturn(ResponseEntity.ok(expectedResponse));

        JiraIssueResponse actualResponse = jiraClientGateway.createIssue(request);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(jiraClient).createIssue(request);
    }

    @Test
    void updateIssue_CallsJiraClient() {
        JiraIssueRequest request = new JiraIssueRequest();
        when(jiraClient.updateIssue(TEST_ISSUE_ID, request)).thenReturn(ResponseEntity.noContent().build());

        jiraClientGateway.updateIssue(TEST_ISSUE_ID, request);

        verify(jiraClient).updateIssue(TEST_ISSUE_ID, request);
    }

    @Test
    void getIssue_ReturnsIssueResponse() {
        when(jiraClient.getIssue(TEST_ISSUE_ID)).thenReturn(ResponseEntity.ok(expectedResponse));

        JiraIssueResponse actualResponse = jiraClientGateway.getIssue(TEST_ISSUE_ID);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(jiraClient).getIssue(TEST_ISSUE_ID);
    }

    @Test
    void searchIssues_ReturnsSearchResponse() {
        String jqlQuery = "project=TEST";
        int startAt = 0;
        int maxResults = 50;
        String fields = "summary,status,assignee";
        JiraSearchResponse expectedResponse = new JiraSearchResponse();
        when(jiraClient.searchIssues(jqlQuery, startAt, maxResults, fields)).thenReturn(ResponseEntity.ok(expectedResponse));

        JiraSearchResponse actualResponse = jiraClientGateway.searchIssues(jqlQuery, startAt, maxResults, fields);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(jiraClient).searchIssues(jqlQuery, startAt, maxResults, fields);
    }
}