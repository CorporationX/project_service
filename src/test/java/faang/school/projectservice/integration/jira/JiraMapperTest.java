package faang.school.projectservice.integration.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import faang.school.projectservice.integration.jira.dto.request.JiraIssueRequest;
import faang.school.projectservice.integration.jira.dto.response.JiraIssueResponse;
import faang.school.projectservice.integration.jira.mapper.JiraMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JiraMapper Tests")
class JiraMapperTest {

    private JiraMapper jiraMapper;

    private Task testTask;
    private Project testProject;
    private Issue mockIssue;

    @BeforeEach
    void setUp() {
        jiraMapper = new JiraMapper();
        
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
            .status(TaskStatus.IN_PROGRESS)
            .performerUserId(100L)
            .reporterUserId(101L)
            .project(testProject)
            .build();

        mockIssue = mock(Issue.class);
        lenient().when(mockIssue.getKey()).thenReturn("TEST-123");
        lenient().when(mockIssue.getId()).thenReturn(123L);
        lenient().when(mockIssue.getSummary()).thenReturn("Test Issue");
        lenient().when(mockIssue.getDescription()).thenReturn("Test Issue Description");

        Status status = mock(Status.class);
        lenient().when(status.getName()).thenReturn("In Progress");
        lenient().when(mockIssue.getStatus()).thenReturn(status);
    }

    // ==========================================
    // Domain → Jira DTO Mapping Tests
    // ==========================================

    @Test
    @DisplayName("Should map Task to JiraIssueRequest")
    void toJiraRequest_Success() {
        JiraIssueRequest request = jiraMapper.toJiraRequest(testTask);

        assertNotNull(request);
        assertNotNull(request.getFields());
        assertEquals("1", request.getFields().getProject().getKey());
        assertEquals("Task", request.getFields().getIssuetype().getName());
        assertEquals("Test Task", request.getFields().getSummary());
        assertEquals("Test Description", request.getFields().getDescription());
    }

    @Test
    @DisplayName("Should map Task with parent to JiraIssueRequest")
    void toJiraRequest_WithParentTask() {
        Task parentTask = Task.builder()
            .id(100L)
            .jiraIssueKey("TEST-100")
            .build();
        testTask.setParentTask(parentTask);

        JiraIssueRequest request = jiraMapper.toJiraRequest(testTask);

        assertNotNull(request);
        assertNotNull(request.getFields().getParent());
        assertEquals("TEST-100", request.getFields().getParent().getKey());
    }

    @Test
    @DisplayName("Should throw exception when Task has no project")
    void toJiraRequestNoProjectThrowsException() {
        testTask.setProject(null);

        assertThrows(IllegalArgumentException.class, () -> {
            jiraMapper.toJiraRequest(testTask);
        });
    }

    @Test
    @DisplayName("Should map Task to IssueInput")
    void toIssueInput_Success() {
        IssueInput issueInput = jiraMapper.toIssueInput(testTask);

        assertNotNull(issueInput);
    }

    @Test
    @DisplayName("Should throw exception when Task has no project for IssueInput")
    void toIssueInputNoProjectThrowsException() {
        testTask.setProject(null);

        assertThrows(IllegalArgumentException.class, () -> {
            jiraMapper.toIssueInput(testTask);
        });
    }

    // ==========================================
    // Jira → Domain Mapping Tests
    // ==========================================

    @Test
    @DisplayName("Should map Jira Issue to Task")
    void toTaskFromIssueSuccess() {
        Task task = jiraMapper.toTask(mockIssue);

        assertNotNull(task);
        assertEquals("TEST-123", task.getJiraIssueKey());
        assertEquals("123", task.getJiraIssueId());
        assertEquals("Test Issue", task.getName());
        assertEquals("Test Issue Description", task.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    @DisplayName("Should map JiraIssueResponse to Task")
    void toTaskFromJiraIssueResponseSuccess() {
        JiraIssueResponse response = JiraIssueResponse.builder()
            .key("TEST-123")
            .id("123")
            .fields(JiraIssueResponse.Fields.builder()
                .summary("Test Issue")
                .description("Test Issue Description")
                .status(JiraIssueResponse.Status.builder()
                    .name("In Progress")
                    .build())
                .build())
            .build();

        Task task = jiraMapper.toTask(response);

        assertNotNull(task);
        assertEquals("TEST-123", task.getJiraIssueKey());
        assertEquals("123", task.getJiraIssueId());
        assertEquals("Test Issue", task.getName());
        assertEquals("Test Issue Description", task.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    @DisplayName("Should handle null status in Issue")
    void toTask_WithNullStatus() {
        when(mockIssue.getStatus()).thenReturn(null);

        Task task = jiraMapper.toTask(mockIssue);

        assertNotNull(task);
        assertNull(task.getStatus());
    }

    @Test
    @DisplayName("Should handle null fields in Issue")
    void toTask_WithNullFields() {
        when(mockIssue.getSummary()).thenReturn(null);
        when(mockIssue.getDescription()).thenReturn(null);
        when(mockIssue.getStatus()).thenReturn(null);

        Task task = jiraMapper.toTask(mockIssue);

        assertNotNull(task);
        assertNull(task.getName());
        assertNull(task.getDescription());
        assertNull(task.getStatus());
    }

    // ==========================================
    // Status Mapping Tests
    // ==========================================

    @Test
    @DisplayName("Should convert TaskStatus to Jira status")
    void toJiraStatus_AllStatuses() {
        assertEquals("To Do", jiraMapper.toJiraStatus(TaskStatus.TODO));
        assertEquals("In Progress", jiraMapper.toJiraStatus(TaskStatus.IN_PROGRESS));
        assertEquals("In Review", jiraMapper.toJiraStatus(TaskStatus.REVIEW));
        assertEquals("Testing", jiraMapper.toJiraStatus(TaskStatus.TESTING));
        assertEquals("Done", jiraMapper.toJiraStatus(TaskStatus.DONE));
        assertEquals("Cancelled", jiraMapper.toJiraStatus(TaskStatus.CANCELLED));
    }

    @Test
    @DisplayName("Should return default status for null")
    void toJiraStatusNullReturnsDefault() {
        assertEquals("To Do", jiraMapper.toJiraStatus(null));
    }

    @Test
    @DisplayName("Should convert Jira status to TaskStatus")
    void fromJiraStatus_AllStatuses() {
        assertEquals(TaskStatus.TODO, jiraMapper.fromJiraStatus("To Do"));
        assertEquals(TaskStatus.TODO, jiraMapper.fromJiraStatus("Open"));
        assertEquals(TaskStatus.TODO, jiraMapper.fromJiraStatus("Backlog"));
        assertEquals(TaskStatus.TODO, jiraMapper.fromJiraStatus("New"));
        
        assertEquals(TaskStatus.IN_PROGRESS, jiraMapper.fromJiraStatus("In Progress"));
        assertEquals(TaskStatus.IN_PROGRESS, jiraMapper.fromJiraStatus("In Development"));
        
        assertEquals(TaskStatus.REVIEW, jiraMapper.fromJiraStatus("In Review"));
        assertEquals(TaskStatus.REVIEW, jiraMapper.fromJiraStatus("Review"));
        
        assertEquals(TaskStatus.TESTING, jiraMapper.fromJiraStatus("Testing"));
        assertEquals(TaskStatus.TESTING, jiraMapper.fromJiraStatus("Test"));
        
        assertEquals(TaskStatus.DONE, jiraMapper.fromJiraStatus("Done"));
        assertEquals(TaskStatus.DONE, jiraMapper.fromJiraStatus("Closed"));
        assertEquals(TaskStatus.DONE, jiraMapper.fromJiraStatus("Resolved"));
        assertEquals(TaskStatus.DONE, jiraMapper.fromJiraStatus("Completed"));
        
        assertEquals(TaskStatus.CANCELLED, jiraMapper.fromJiraStatus("Cancelled"));
        assertEquals(TaskStatus.CANCELLED, jiraMapper.fromJiraStatus("Canceled"));
        assertEquals(TaskStatus.CANCELLED, jiraMapper.fromJiraStatus("Blocked"));
        assertEquals(TaskStatus.CANCELLED, jiraMapper.fromJiraStatus("On Hold"));
        assertEquals(TaskStatus.CANCELLED, jiraMapper.fromJiraStatus("Impediment"));
    }

    @Test
    @DisplayName("Should return default for unknown Jira status")
    void fromJiraStatusUnknownReturnsDefault() {
        assertEquals(TaskStatus.TODO, jiraMapper.fromJiraStatus("Unknown Status"));
    }

    @Test
    @DisplayName("Should return default for null Jira status")
    void fromJiraStatusNullReturnsDefault() {
        assertEquals(TaskStatus.TODO, jiraMapper.fromJiraStatus(null));
    }

    @Test
    @DisplayName("Should handle case-insensitive status conversion")
    void fromJiraStatus_CaseInsensitive() {
        assertEquals(TaskStatus.IN_PROGRESS, jiraMapper.fromJiraStatus("IN PROGRESS"));
        assertEquals(TaskStatus.IN_PROGRESS, jiraMapper.fromJiraStatus("in progress"));
        assertEquals(TaskStatus.IN_PROGRESS, jiraMapper.fromJiraStatus("In Progress"));
    }

    // ==========================================
    // Transition Mapping Tests
    // ==========================================

    @Test
    @DisplayName("Should get transition name for status")
    void getTransitionName_Success() {
        assertEquals("Start Progress", jiraMapper.getTransitionName(TaskStatus.IN_PROGRESS));
        assertEquals("Submit for Review", jiraMapper.getTransitionName(TaskStatus.REVIEW));
        assertEquals("Done", jiraMapper.getTransitionName(TaskStatus.DONE));
        assertEquals("Cancel", jiraMapper.getTransitionName(TaskStatus.CANCELLED));
        assertNull(jiraMapper.getTransitionName(TaskStatus.TODO));
        assertNull(jiraMapper.getTransitionName(TaskStatus.TESTING));
    }

    @Test
    @DisplayName("Should return null for null status in transition")
    void getTransitionNameNullReturnsNull() {
        assertNull(jiraMapper.getTransitionName(null));
    }

    // ==========================================
    // Date Conversion Tests
    // ==========================================

    @Test
    @DisplayName("Should convert LocalDate to Joda DateTime")
    void toJodaDateTime_Success() {
        LocalDate localDate = LocalDate.of(2024, 1, 15);
        DateTime jodaDate = jiraMapper.toJodaDateTime(localDate);

        assertNotNull(jodaDate);
        assertEquals(2024, jodaDate.getYear());
        assertEquals(1, jodaDate.getMonthOfYear());
        assertEquals(15, jodaDate.getDayOfMonth());
    }

    @Test
    @DisplayName("Should return null for null LocalDate")
    void toJodaDateTimeNullReturnsNull() {
        assertNull(jiraMapper.toJodaDateTime(null));
    }

    @Test
    @DisplayName("Should convert Joda DateTime to LocalDate")
    void fromJodaDateTime_Success() {
        DateTime jodaDate = new DateTime(2024, 1, 15, 0, 0);
        LocalDate localDate = jiraMapper.fromJodaDateTime(jodaDate);

        assertNotNull(localDate);
        assertEquals(2024, localDate.getYear());
        assertEquals(1, localDate.getMonthValue());
        assertEquals(15, localDate.getDayOfMonth());
    }

    @Test
    @DisplayName("Should return null for null Joda DateTime")
    void fromJodaDateTimeNullReturnsNull() {
        assertNull(jiraMapper.fromJodaDateTime(null));
    }

    // ==========================================
    // Edge Cases
    // ==========================================

    @Test
    @DisplayName("Should handle Task with minimal fields")
    void toJiraRequest_MinimalTask() {
        Task minimalTask = Task.builder()
            .name("Minimal Task")
            .project(testProject)
            .performerUserId(100L)
            .reporterUserId(101L)
            .build();

        JiraIssueRequest request = jiraMapper.toJiraRequest(minimalTask);

        assertNotNull(request);
        assertEquals("Minimal Task", request.getFields().getSummary());
        assertNull(request.getFields().getDescription());
    }
}
