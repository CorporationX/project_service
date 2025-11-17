package faang.school.projectservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.integration.jira.oauth.model.JiraOauthTokenRepository;
import faang.school.projectservice.integration.jira.oauth.model.UserJiraOauthToken;
import faang.school.projectservice.integration.jira.queue.config.RabbitMqConfig;
import faang.school.projectservice.integration.jira.service.JiraIntegrationService;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("End-to-End Integration Test")
class JiraIntegrationEndToEndTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @Container
    static RabbitMQContainer rabbitMq = new RabbitMQContainer("rabbitmq:3.12-management")
        .withExposedPorts(5672, 15672);
    
    static {
        postgres.start();
        rabbitMq.start();
    }
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.rabbitMq.host", rabbitMq::getHost);
        registry.add("spring.rabbitMq.port", rabbitMq::getFirstMappedPort);
        registry.add("spring.rabbitMq.username", () -> "guest");
        registry.add("spring.rabbitMq.password", () -> "guest");
        
        registry.add("jira.scheduled.enabled", () -> "false");
        registry.add("jira.Oauth.enable", () -> "false");
        registry.add("jira.system.base-url", () -> "http://localhost:8080");
        registry.add("jira.system.username", () -> "test");
        registry.add("jira.system.api-token", () -> "test-token");
        registry.add("spring.liquibase.enabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "false");
    }
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private JiraOauthTokenRepository tokenRepository;
    
    @Autowired
    private RabbitAdmin rabbitAdmin;
    
    @MockBean
    private JiraIntegrationService jiraIntegrationService;
    
    @MockBean
    @Qualifier("jiraOauthWebClient")
    private WebClient jiraOauthWebClient;
    
    @MockBean
    private JiraRestClient jiraRestClient;
    
    private Project testProject;
    private Long testUserId = 100L;
    
    @BeforeEach
    @Transactional
    void setUp() {
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        
        rabbitAdmin.purgeQueue(RabbitMqConfig.TASK_CREATE_QUEUE, false);
        rabbitAdmin.purgeQueue(RabbitMqConfig.TASK_UPDATE_QUEUE, false);
        rabbitAdmin.purgeQueue(RabbitMqConfig.TASK_DELETE_QUEUE, false);
        
        testProject = Project.builder()
            .name("Test Project")
            .status(ProjectStatus.IN_PROGRESS)
            .visibility(ProjectVisibility.PUBLIC)
            .build();
        testProject = projectRepository.save(testProject);
        
        UserJiraOauthToken token = tokenRepository.findByUserId(testUserId)
            .orElse(new UserJiraOauthToken());
        token.setUserId(testUserId);
        token.setAccessToken("test-access-token");
        token.setRefreshToken("test-refresh-token");
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        tokenRepository.save(token);
        
        when(jiraIntegrationService.createTaskAsUser(anyLong(), any(Task.class)))
            .thenAnswer(invocation -> {
                Task task = invocation.getArgument(1);
                task.setJiraIssueKey("TEST-" + task.getId());
                return task.getJiraIssueKey();
            });
        
        when(jiraIntegrationService.createTaskAsSystem(any(Task.class)))
            .thenAnswer(invocation -> {
                Task task = invocation.getArgument(0);
                task.setJiraIssueKey("TEST-" + task.getId());
                return task.getJiraIssueKey();
            });
        
        doNothing().when(jiraIntegrationService)
            .updateTaskAsUser(anyLong(), anyString(), any(Task.class));
        
        doNothing().when(jiraIntegrationService)
            .updateTaskAsSystem(anyString(), any(Task.class));
    }
    
    // ==========================================
    // CREATE Flow Tests
    // ==========================================
    
    @Test
    @DisplayName("Should complete full task creation flow")
    void testFullTaskCreationFlow() throws Exception {
        TaskDto createRequest = TaskDto.builder()
            .name("E2E Test Task")
            .description("This is an end-to-end test task")
            .status(TaskStatus.TODO)
            .performerUserId(100L)
            .reporterUserId(100L)
            .build();
        
        MvcResult result = mockMvc.perform(post("/api/projects/{projectId}/tasks", testProject.getId())
                .header("X-User-Id", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.name").value("E2E Test Task"))
            .andExpect(jsonPath("$.status").value("TODO"))
            .andReturn();
        
        TaskDto response = objectMapper.readValue(
            result.getResponse().getContentAsString(), 
            TaskDto.class
        );
        
        Task savedTask = taskRepository.findById(response.getId())
            .orElseThrow(() -> new AssertionError("Task not found in database"));
        
        assertEquals("E2E Test Task", savedTask.getName());
        assertEquals(TaskStatus.TODO, savedTask.getStatus());
        
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                verify(jiraIntegrationService, atLeastOnce())
                    .createTaskAsUser(eq(testUserId), any(Task.class));
            });
    }
    
    // ==========================================
    // UPDATE Flow Tests
    // ==========================================
    
    @Test
    @DisplayName("Should complete full task update flow")
    void testFullTaskUpdateFlow() throws Exception {
        Task existingTask = Task.builder()
            .name("Original Title")
            .description("Original Description")
            .status(TaskStatus.TODO)
            .performerUserId(100L)
            .reporterUserId(100L)
            .project(testProject)
            .jiraIssueKey("TEST-999")
            .build();
        existingTask = taskRepository.save(existingTask);
        taskRepository.flush();
        
        TaskDto updateRequest = TaskDto.builder()
            .name("Updated Title")
            .description("Updated Description")
            .status(TaskStatus.IN_PROGRESS)
            .performerUserId(100L)
            .reporterUserId(100L)
            .build();
        
        mockMvc.perform(put("/api/projects/{projectId}/tasks/{taskId}", 
                testProject.getId(), existingTask.getId())
                .header("X-User-Id", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Title"))
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        
        Task updatedTask = taskRepository.findById(existingTask.getId())
            .orElseThrow();
        
        assertEquals("Updated Title", updatedTask.getName());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
        
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                verify(jiraIntegrationService, atLeastOnce())
                    .updateTaskAsUser(eq(testUserId), eq("TEST-999"), any(Task.class));
            });
    }
    
    // ==========================================
    // READ Flow Tests
    // ==========================================
    
    @Test
    @DisplayName("Should get tasks with filters")
    @Transactional
    void testGetTasksWithFilters() throws Exception {
        createTestTask("Task 1", TaskStatus.TODO, 100L);
        createTestTask("Task 2", TaskStatus.IN_PROGRESS, 200L);
        createTestTask("Task 3", TaskStatus.IN_PROGRESS, 100L);
        createTestTask("Task 4", TaskStatus.DONE, 200L);
        
        mockMvc.perform(get("/api/projects/{projectId}/tasks", testProject.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)));
        
        mockMvc.perform(get("/api/projects/{projectId}/tasks", testProject.getId())
                .param("status", "IN_PROGRESS"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
        
        mockMvc.perform(get("/api/projects/{projectId}/tasks", testProject.getId())
                .param("performerUserId", "100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }
    
    // ==========================================
    // SYNC Flow Tests
    // ==========================================
    
    @Test
    @DisplayName("Should sync project with Jira")
    void testProjectSync() throws Exception {
        JiraIntegrationService.SyncResult mockResult = new JiraIntegrationService.SyncResult();
        mockResult.setTotalTasks(10);
        mockResult.setCreatedTasks(5);
        mockResult.setUpdatedTasks(3);
        mockResult.setFailedTasks(2);
        
        when(jiraIntegrationService.syncProject(testProject.getId()))
            .thenReturn(mockResult);
        
        mockMvc.perform(post("/api/jira/sync/project/{projectId}", testProject.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalTasks").value(10))
            .andExpect(jsonPath("$.createdTasks").value(5))
            .andExpect(jsonPath("$.updatedTasks").value(3))
            .andExpect(jsonPath("$.failedTasks").value(2));
    }
    
    // ==========================================
    // Health Check Tests
    // ==========================================
    
    @Test
    @DisplayName("Should check integration health")
    void testHealthCheck() throws Exception {
        when(jiraIntegrationService.checkJiraHealth()).thenReturn(true);
        
        mockMvc.perform(get("/api/jira/sync/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.healthy").value(true))
            .andExpect(jsonPath("$.message").value("Jira is available"));
        
        when(jiraIntegrationService.checkJiraHealth()).thenReturn(false);
        
        mockMvc.perform(get("/api/jira/sync/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.healthy").value(false))
            .andExpect(jsonPath("$.message").value("Jira is unavailable"));
    }
    
    // ==========================================
    // DELETE Flow Tests
    // ==========================================
    
    @Test
    @DisplayName("Should delete task")
    @Transactional
    void testDeleteTask() throws Exception {
        Task task = createTestTask("Task to delete", TaskStatus.TODO, 100L);
        task.setJiraIssueKey("TEST-DELETE");
        taskRepository.save(task);
        
        doNothing().when(jiraIntegrationService)
            .deleteTaskAsUser(testUserId, "TEST-DELETE");
        
        mockMvc.perform(delete("/api/projects/{projectId}/tasks/{taskId}", 
                testProject.getId(), task.getId())
                .header("X-User-Id", testUserId))
            .andExpect(status().isNoContent());
        
        assertFalse(taskRepository.existsById(task.getId()));
        
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                verify(jiraIntegrationService, atLeastOnce())
                    .deleteTaskAsUser(eq(testUserId), eq("TEST-DELETE"));
            });
    }
    
    // ==========================================
    // Validation Tests
    // ==========================================
    
    @Test
    @DisplayName("Should handle validation errors")
    void testValidationErrors() throws Exception {
        TaskDto invalidRequest = TaskDto.builder().build();
        
        mockMvc.perform(post("/api/projects/{projectId}/tasks", testProject.getId())
                .header("X-User-Id", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }
    
    // ==========================================
    // Helper Methods
    // ==========================================
    
    private Task createTestTask(String name, TaskStatus status, Long performerUserId) {
        Task task = Task.builder()
            .name(name)
            .status(status)
            .performerUserId(performerUserId)
            .reporterUserId(100L)
            .project(testProject)
            .build();
        return taskRepository.save(task);
    }
}

