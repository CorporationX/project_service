package school.faang.project_service.controller;

import faang.school.projectservice.controller.JiraController;
import faang.school.projectservice.dto.jira.request.JiraIssueRequest;
import faang.school.projectservice.dto.jira.response.JiraIssueResponse;
import faang.school.projectservice.dto.jira.response.JiraSearchResponse;
import faang.school.projectservice.gateway.JiraClientGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JiraControllerTest {
    public static final String TEST_ISSUE_ID = "TEST-125";

    @Mock
    private JiraClientGateway jiraClientGateway;

    @InjectMocks
    private JiraController jiraController;

    @Test
    void createIssue_ReturnsIssueResponse() {
        JiraIssueRequest request = new JiraIssueRequest();
        JiraIssueResponse expectedResponse = new JiraIssueResponse();

        when(jiraClientGateway.createIssue(request)).thenReturn(expectedResponse);

        ResponseEntity<JiraIssueResponse> response = jiraController.createIssue(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        verify(jiraClientGateway).createIssue(request);
    }

    @Test
    void updateIssue_ReturnsNoContent() {
        JiraIssueRequest request = new JiraIssueRequest();

        doNothing().when(jiraClientGateway).updateIssue(TEST_ISSUE_ID, request);

        ResponseEntity<Void> response = jiraController.updateIssue(TEST_ISSUE_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(jiraClientGateway).updateIssue(TEST_ISSUE_ID, request);
    }

    @Test
    void getIssue_ReturnsIssueResponse() {
        JiraIssueResponse expectedResponse = new JiraIssueResponse();

        when(jiraClientGateway.getIssue(TEST_ISSUE_ID)).thenReturn(expectedResponse);

        ResponseEntity<JiraIssueResponse> response = jiraController.getIssue(TEST_ISSUE_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        verify(jiraClientGateway).getIssue(TEST_ISSUE_ID);
    }

    @Test
    void getIssues_ReturnsSearchResponse() {
        String jqlQuery = "project=TEST";
        int startAt = 0;
        int maxResults = 50;
        String fields = "summary,status,assignee";
        JiraSearchResponse expectedResponse = new JiraSearchResponse();

        when(jiraClientGateway.searchIssues(jqlQuery, startAt, maxResults, fields)).thenReturn(expectedResponse);

        ResponseEntity<JiraSearchResponse> response = jiraController.getIssues(jqlQuery, startAt, maxResults, fields);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        verify(jiraClientGateway).searchIssues(jqlQuery, startAt, maxResults, fields);
    }
}