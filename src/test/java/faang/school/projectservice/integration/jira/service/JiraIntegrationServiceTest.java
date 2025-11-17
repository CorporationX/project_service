package faang.school.projectservice.integration.jira.service;

import com.atlassian.jira.rest.client.api.domain.Issue;
import faang.school.projectservice.integration.jira.client.JiraOAuthClient;
import faang.school.projectservice.integration.jira.JiraSystemClient;
import faang.school.projectservice.integration.jira.dto.request.JiraIssueRequest;
import faang.school.projectservice.integration.jira.exception.JiraOAuthException;
import faang.school.projectservice.integration.jira.dto.response.OAuthTokenResponse;
import faang.school.projectservice.integration.jira.event.JiraEventPublisher;
import faang.school.projectservice.integration.jira.mapper.JiraMapper;
import faang.school.projectservice.integration.jira.metrics.JiraMetricsService;
import faang.school.projectservice.integration.jira.oauth.JiraOAuthTokenManager;
import faang.school.projectservice.integration.jira.oauth.model.JiraOAuthTokenRepository;
import faang.school.projectservice.integration.jira.oauth.model.UserJiraOAuthToken;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@DisplayName("JiraIntegrationService Tests")
class JiraIntegrationServiceTest {
    
    @Mock
    private JiraSystemClient systemClient;
    
    @Mock
    private JiraOAuthClient oauthClient;
    
    @Mock
    private JiraOAuthTokenManager tokenManager;
    
    @Mock
    private JiraOAuthTokenRepository tokenRepository;
    
    @Mock
    private JiraOAuthService oauthService;
    
    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private JiraMapper jiraMapper;
    
    @Mock
    private JiraEventPublisher eventPublisher;
    
    @Mock
    private JiraMetricsService metricsService;
    
    @InjectMocks
    private JiraIntegrationService integrationService;
    
    private Task testTask;
    private UserJiraOAuthToken validToken;
    private Issue mockIssue;
    private Project testProject;
    
    @BeforeEach
    void setUp() {
        testProject = Project.builder()
            .id(1L)
            .name("Test Project")
            .status(ProjectStatus.IN_PROGRESS)
            .visibility(ProjectVisibility.PUBLIC)
            .build();
        
        testTask = Task.builder()
            .id(1L)
            .name("Test Task")
            .description("Test Description")
            .status(TaskStatus.TODO)
            .performerUserId(100L)
            .reporterUserId(100L)
            .project(testProject)
            .build();
        
        validToken = new UserJiraOAuthToken();
        validToken.setUserId(100L);
        validToken.setAccessToken("test-access-token");
        validToken.setRefreshToken("test-refresh-token");
        validToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        
        mockIssue = mock(Issue.class);
        lenient().when(mockIssue.getKey()).thenReturn("TEST-123");
        lenient().when(mockIssue.getId()).thenReturn(123L);
        
        // Mock JiraMetricsService
        SimpleMeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();
        Metrics.globalRegistry.add(simpleMeterRegistry);
        Timer.Sample mockSample = Timer.start(simpleMeterRegistry);
        lenient().when(metricsService.startApiRequestTimer()).thenReturn(mockSample);
        lenient().doNothing().when(metricsService).recordTaskCreated(anyString());
        lenient().doNothing().when(metricsService).recordTaskUpdated(anyString());
        lenient().doNothing().when(metricsService).recordTaskDeleted(anyString());
        lenient().doNothing().when(metricsService).recordTaskSynced(anyInt());
        lenient().doNothing().when(metricsService).recordTaskFailed(anyString(), anyString());
        lenient().doNothing().when(metricsService).recordOAuthFallback();
        lenient().doNothing().when(metricsService).recordApiError(anyString());
        lenient().doNothing().when(metricsService).recordApiRequestDuration(any(Timer.Sample.class), anyString());
    }
    
    // ==========================================
    // CREATE Operations Tests
    // ==========================================
    
    @Test
    @DisplayName("Should create task via OAuth when user has valid token")
    void createTaskAsUser_WithValidToken_UsesOAuthClient() {
        Long userId = 100L;
        when(tokenManager.getValidToken(userId)).thenReturn("test-access-token");
        
        JiraIssueRequest createRequest = new JiraIssueRequest();
        when(jiraMapper.toJiraRequest(testTask)).thenReturn(createRequest);
        
        when(oauthClient.createIssue("test-access-token", createRequest))
            .thenReturn("TEST-123");
        
        String jiraKey = integrationService.createTaskAsUser(userId, testTask);
        
        assertEquals("TEST-123", jiraKey);
        assertEquals("TEST-123", testTask.getJiraIssueKey());
        
        verify(oauthClient).createIssue(anyString(), eq(createRequest));
        verify(taskRepository).save(testTask);
        verify(eventPublisher).publishTaskCreated(testTask, "TEST-123");
    }
    
    @Test
    @DisplayName("Should fallback to system client when user has no token")
    void createTaskAsUser_WithoutToken_FallsBackToSystemClient() {
        Long userId = 100L;
        when(tokenManager.getValidToken(userId))
            .thenThrow(new JiraOAuthException("No token"));
        
        com.atlassian.jira.rest.client.api.domain.input.IssueInput issueInput = 
            mock(com.atlassian.jira.rest.client.api.domain.input.IssueInput.class);
        when(jiraMapper.toIssueInput(testTask)).thenReturn(issueInput);
        when(systemClient.createIssue(issueInput)).thenReturn("TEST-123");
        
        String jiraKey = integrationService.createTaskAsUser(userId, testTask);
        
        assertEquals("TEST-123", jiraKey);
        verify(systemClient).createIssue(issueInput);
    }
    
    // ==========================================
    // UPDATE Operations Tests
    // ==========================================
    
    @Test
    @DisplayName("Should update task via OAuth when available")
    void updateTaskAsUser_WithValidToken_UsesOAuthClient() {
        Long userId = 100L;
        testTask.setJiraIssueKey("TEST-123");
        
        when(tokenManager.getValidToken(userId)).thenReturn("test-access-token");
        JiraIssueRequest updateRequest = new JiraIssueRequest();
        when(jiraMapper.toJiraRequest(testTask)).thenReturn(updateRequest);
        when(taskRepository.findByJiraIssueKey("TEST-123"))
            .thenReturn(Optional.of(testTask))  // First call - get old task
            .thenReturn(Optional.of(testTask)); // Second call - get updated task
        
        integrationService.updateTaskAsUser(userId, "TEST-123", testTask);
        
        verify(oauthClient).updateIssue("test-access-token", "TEST-123", updateRequest);
        verify(eventPublisher).publishTaskUpdated(eq(testTask), any(), eq("TEST-123"));
    }
    
    // ==========================================
    // SEARCH Operations Tests
    // ==========================================
    
    @Test
    @DisplayName("Should search tasks in Jira")
    void getProjectTasks_Success() {
        Long projectId = 1L;
        TaskStatus status = TaskStatus.IN_PROGRESS;
        String assignee = "user@example.com";
        
        List<Issue> issues = Arrays.asList(mockIssue);
        when(systemClient.searchIssues(anyString(), eq(100)))
            .thenReturn(issues);
        
        Task mappedTask = Task.builder().name("Mapped Task").build();
        when(jiraMapper.toTask(mockIssue)).thenReturn(mappedTask);
        
        List<Task> tasks = integrationService.getProjectTasks(projectId, status, assignee);
        
        assertEquals(1, tasks.size());
        verify(systemClient).searchIssues(contains("project = " + projectId), eq(100));
    }
    
    // ==========================================
    // SYNC Operations Tests
    // ==========================================
    
    @Test
    @DisplayName("Should sync project with Jira")
    void syncProject_Success() {
        Long projectId = 1L;
        
        List<Issue> jiraIssues = Arrays.asList(mockIssue);
        when(systemClient.searchIssues("project = " + projectId, 1000))
            .thenReturn(jiraIssues);
        
        List<Task> ourTasks = Arrays.asList();
        when(taskRepository.findAllByProjectId(projectId)).thenReturn(ourTasks);
        
        Task newTask = Task.builder()
            .name("New Task")
            .project(testProject)
            .build();
        when(jiraMapper.toTask(mockIssue)).thenReturn(newTask);
        
        JiraIntegrationService.SyncResult result = integrationService.syncProject(projectId);
        
        assertEquals(1, result.getTotalTasks());
        assertEquals(1, result.getCreatedTasks());
        assertEquals(0, result.getUpdatedTasks());
        assertEquals(0, result.getFailedTasks());
        
        verify(taskRepository).save(newTask);
    }
    
    // ==========================================
    // OAuth Token Management Tests
    // ==========================================
    
    @Test
    @DisplayName("Should refresh OAuth token")
    void refreshOAuthToken_Success() {
        Long userId = 100L;
        when(tokenRepository.findByUserId(userId))
            .thenReturn(Optional.of(validToken));
        
        OAuthTokenResponse tokenResponse = OAuthTokenResponse.builder()
            .accessToken("new-access-token")
            .refreshToken("new-refresh-token")
            .expiresIn(3600L)
            .build();
        
        when(oauthService.refreshAccessToken(validToken.getRefreshToken()))
            .thenReturn(tokenResponse);
        
        integrationService.refreshOAuthToken(userId);
        
        assertEquals("new-access-token", validToken.getAccessToken());
        assertEquals("new-refresh-token", validToken.getRefreshToken());
        verify(tokenRepository).save(validToken);
    }
    
    // ==========================================
    // Health Check Tests
    // ==========================================
    
    @Test
    @DisplayName("Should return true when Jira is healthy")
    void checkJiraHealth_Healthy() {
        when(systemClient.getIssue("TEST-1")).thenReturn(mockIssue);
        
        boolean healthy = integrationService.checkJiraHealth();
        
        assertTrue(healthy);
    }
    
    @Test
    @DisplayName("Should return false when Jira is down")
    void checkJiraHealth_Unhealthy() {
        when(systemClient.getIssue("TEST-1"))
            .thenThrow(new RuntimeException("Connection failed"));
        
        boolean healthy = integrationService.checkJiraHealth();
        
        assertFalse(healthy);
    }
    
    @Test
    @DisplayName("Should get integration status")
    void getIntegrationStatus_Success() {
        when(systemClient.getIssue(anyString())).thenReturn(mockIssue);
        when(tokenRepository.countActiveTokens(any(LocalDateTime.class)))
            .thenReturn(5L);
        
        Task syncedTask = Task.builder().jiraIssueKey("JIRA-1").build();
        Task unsyncedTask1 = Task.builder().jiraIssueKey(null).build();
        Task unsyncedTask2 = Task.builder().jiraIssueKey(null).build();
        when(taskRepository.findAll()).thenReturn(Arrays.asList(syncedTask, unsyncedTask1, unsyncedTask2));
        
        JiraIntegrationService.IntegrationStatus status = integrationService.getIntegrationStatus();
        
        assertTrue(status.isSystemClientAvailable());
        assertEquals(5L, status.getActiveOAuthTokens());
        assertEquals(2L, status.getUnsyncedTasks());
    }
}

