package faang.school.projectservice.integration.jira.queue;

import faang.school.projectservice.integration.jira.queue.config.RabbitMQConfig;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("RabbitMq Integration Tests")
class RabbitMQIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @Container
    static RabbitMQContainer rabbitMqContainer = new RabbitMQContainer("rabbitmq:3.12-management")
        .withExposedPorts(5672, 15672);
    
    static {
        postgres.start();
        rabbitMqContainer.start();
    }
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.rabbitMq.host", rabbitMqContainer::getHost);
        registry.add("spring.rabbitMq.port", rabbitMqContainer::getFirstMappedPort);
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
        // MinIO configuration - disable MinIO in tests to prevent connection attempts
        registry.add("minio.enabled", () -> "false");
    }
    
    @Autowired
    private JiraTaskQueue producer;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private RabbitAdmin rabbitAdmin;
    
    @MockBean
    private JiraIntegrationService integrationService;
    
    @MockBean
    @Qualifier("jiraOauthWebClient")
    private WebClient jiraOauthWebClient;
    
    @MockBean
    private JiraRestClient jiraRestClient;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    private Task testTask;
    private Project testProject;
    
    @BeforeEach
    void setUp() {
        rabbitAdmin.purgeQueue(RabbitMQConfig.TASK_CREATE_QUEUE, false);
        rabbitAdmin.purgeQueue(RabbitMQConfig.TASK_UPDATE_QUEUE, false);
        rabbitAdmin.purgeQueue(RabbitMQConfig.TASK_DELETE_QUEUE, false);
        
        testProject = Project.builder()
            .name("Test Project")
            .status(ProjectStatus.IN_PROGRESS)
            .visibility(ProjectVisibility.PUBLIC)
            .build();
        testProject = projectRepository.save(testProject);
        
        testTask = Task.builder()
            .id(1L)
            .name("Test Task")
            .description("Test Description")
            .status(TaskStatus.TODO)
            .performerUserId(100L)
            .reporterUserId(100L)
            .project(testProject)
            .build();
        testTask = taskRepository.save(testTask);
    }
    
    // ==========================================
    // CREATE Tests
    // ==========================================
    
    @Test
    @DisplayName("Should send and receive task creation message")
    void testTaskCreationQueue() {
        Long userId = 100L;
        when(integrationService.createTaskAsUser(eq(userId), any(Task.class)))
            .thenReturn("TEST-123");
        
        producer.queueTaskCreation(testTask, userId);
        
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                verify(integrationService, times(1))
                    .createTaskAsUser(eq(userId), any(Task.class));
            });
    }
    
    // ==========================================
    // UPDATE Tests
    // ==========================================
    
    @Test
    @DisplayName("Should send and receive task update message")
    void testTaskUpdateQueue() {
        Long userId = 100L;
        testTask.setJiraIssueKey("TEST-123");
        taskRepository.save(testTask);
        
        doNothing().when(integrationService)
            .updateTaskAsUser(eq(userId), eq("TEST-123"), any(Task.class));
        
        producer.queueTaskUpdate(testTask, userId);
        
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                verify(integrationService, times(1))
                    .updateTaskAsUser(eq(userId), eq("TEST-123"), any(Task.class));
            });
    }
    
    // ==========================================
    // DELETE Tests
    // ==========================================
    
    @Test
    @DisplayName("Should handle task deletion")
    void testTaskDeletionQueue() {
        final Long userId = 100L;
        final String jiraIssueKey = "TEST-123";
        
        doNothing().when(integrationService)
            .deleteTaskAsUser(userId, jiraIssueKey);
        
        producer.queueTaskDeletion(testTask.getId(), jiraIssueKey, userId);
        
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                verify(integrationService, times(1))
                    .deleteTaskAsUser(eq(userId), eq(jiraIssueKey));
            });
    }
    
    // ==========================================
    // SYNC Tests
    // ==========================================
    
    @Test
    @DisplayName("Should handle project sync")
    void testProjectSyncQueue() {
        JiraIntegrationService.SyncResult syncResult = new JiraIntegrationService.SyncResult();
        syncResult.setTotalTasks(10);
        syncResult.setCreatedTasks(5);
        syncResult.setUpdatedTasks(3);
        syncResult.setFailedTasks(2);
        
        when(integrationService.syncProject(testProject.getId()))
            .thenReturn(syncResult);
        
        final Long userId = 100L;
        producer.queueProjectSync(testProject.getId(), userId);
        
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                verify(integrationService, times(1))
                    .syncProject(eq(testProject.getId()));
            });
    }
    
    // ==========================================
    // Retry Tests
    // ==========================================
    
    @Test
    @DisplayName("Should retry failed messages")
    void testRetryMechanism() {
        Long userId = 100L;
        
        when(integrationService.createTaskAsUser(eq(userId), any(Task.class)))
            .thenThrow(new RuntimeException("Jira unavailable"))
            .thenThrow(new RuntimeException("Jira still unavailable"))
            .thenReturn("TEST-123");
        
        producer.queueTaskCreation(testTask, userId);
        
        await()
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                verify(integrationService, atLeast(3))
                    .createTaskAsUser(eq(userId), any(Task.class));
            });
    }
    
    // ==========================================
    // Bulk Tests
    // ==========================================
    
    @Test
    @DisplayName("Should handle bulk update")
    void testBulkUpdate() {
        Long userId = 100L;
        Task task1 = createTask("Task 1");
        Task task2 = createTask("Task 2");
        Task task3 = createTask("Task 3");
        
        doNothing().when(integrationService).updateTaskAsUser(eq(userId), anyString(), any());
        
        producer.queueBulkUpdate(List.of(task1, task2, task3), userId);
        
        await()
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                verify(integrationService, atLeast(3))
                    .updateTaskAsUser(eq(userId), anyString(), any(Task.class));
            });
    }
    
    // ==========================================
    // Helper Methods
    // ==========================================
    
    private Task createTask(String name) {
        Task task = Task.builder()
            .name(name)
            .description("Description")
            .status(TaskStatus.TODO)
            .performerUserId(100L)
            .reporterUserId(100L)
            .project(testProject)
            .jiraIssueKey("TEST-" + name.hashCode())
            .build();
        return taskRepository.save(task);
    }
}
