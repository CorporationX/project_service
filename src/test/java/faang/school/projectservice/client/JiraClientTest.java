package faang.school.projectservice.client;

import faang.school.projectservice.model.jira.JiraProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JiraClientTest {
    @InjectMocks
    private JiraClient jiraClient;
    @Mock
    private RestTemplate restTemplate;
    private JiraProject jiraProject;
    private String issueKey;

    @BeforeEach
    void setUp() {
        jiraProject = JiraProject.builder()
                .username("jiraUsername")
                .password("jiraPassword")
                .url("jiraProjectUrl")
                .build();

        issueKey = "KEY";

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class),
                nullable(String.class))
        ).thenReturn(ResponseEntity.ok("Issue or issues received"));

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class),
                anyString())
        ).thenReturn(ResponseEntity.ok("Issue created"));
    }

    @Test
    void testGetIssue() {
        String response = jiraClient.getIssue(jiraProject, issueKey);

        verify(restTemplate).exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class),
                nullable(String.class));
        assertEquals("Issue or issues received", response);
    }

    @Test
    void testGetIssuesWithFilter() {
        String response = jiraClient.getIssue(jiraProject, issueKey);

        verify(restTemplate).exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class),
                nullable(String.class));
        assertEquals("Issue or issues received", response);
    }

    @Test
    void testCreateIssue() {
        String response = jiraClient.createIssue(jiraProject, "body");

        verify(restTemplate).exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class),
                anyString());
        assertEquals("Issue created", response);
    }

    @Test
    void testUpdateIssue() {
        String response = jiraClient.updateIssue(jiraProject, issueKey, "body");

        verify(restTemplate).exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class),
                anyString());
        assertEquals("Issue fields updated successfully", response);
    }

    @Test
    void testChangeIssueStatus() {
        String response = jiraClient.changeIssueStatus(jiraProject, issueKey, "body");

        verify(restTemplate).exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class),
                anyString());
        assertEquals("Issue status changed successfully", response);
    }

    @Test
    void testCreateIssueLink() {
        String response = jiraClient.createIssueLink(jiraProject, "body");

        verify(restTemplate).exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class),
                anyString());
        assertEquals("Issue link created successfully", response);
    }
}