package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Qualifier;
import faang.school.projectservice.integration.jira.client.JiraOAuthClient;
import faang.school.projectservice.integration.jira.JiraSystemClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Resource Controller Integration Tests")
class ResourceControllerIntegrationTest {
    
    private static boolean dockerAvailable;
    private static PostgreSQLContainer<?> postgres;
    
    static {
        dockerAvailable = checkDockerAvailable();
        if (dockerAvailable) {
            postgres = new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");
            try {
                postgres.start();
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    if (postgres != null && postgres.isRunning()) {
                        postgres.stop();
                    }
                }));
            } catch (Exception e) {
                dockerAvailable = false;
                System.err.println("Не удалось запустить PostgreSQL контейнер: " + e.getMessage());
            }
        }
    }
    
    private static boolean checkDockerAvailable() {
        try {
            org.testcontainers.DockerClientFactory.instance().client();
            return true;
        } catch (Exception e) {
            System.out.println("Docker недоступен. Интеграционные тесты будут пропущены.");
            return false;
        }
    }
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        if (dockerAvailable && postgres != null) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
            registry.add("spring.liquibase.enabled", () -> "false");
            registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
            registry.add("spring.jpa.show-sql", () -> "false");
        }

        // MinIO configuration - disable MinIO in tests to prevent connection attempts
        registry.add("minio.enabled", () -> "false");
        registry.add("minio.endpoint", () -> "http://localhost:9000");
        registry.add("minio.access-key", () -> "test");
        registry.add("minio.secret-key", () -> "test");
        registry.add("minio.bucket-name", () -> "test-bucket");
        registry.add("minio.region", () -> "us-east-1");
        
        // Jira configuration to prevent bean creation errors
        registry.add("jira.oauth.enabled", () -> "false");
        registry.add("jira.system.enabled", () -> "false");
        registry.add("jira.system.base-url", () -> "http://localhost:8080");
        registry.add("jira.system.username", () -> "test");
        registry.add("jira.system.api-token", () -> "test-token");
        registry.add("jira.sync.enabled", () -> "false");
    }
    
    @BeforeEach
    void checkDockerBeforeTest() {
        org.junit.jupiter.api.Assumptions.assumeTrue(dockerAvailable, 
            "Docker недоступен. Интеграционные тесты требуют Docker.");
    }
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private MinioClient minioClient;
    
    @MockBean
    @Qualifier("jiraOAuthWebClient")
    private WebClient jiraOAuthWebClient;
    
    @MockBean
    private JiraOAuthClient jiraOAuthClient;
    
    @MockBean
    private JiraRestClient jiraRestClient;
    
    @MockBean
    private JiraSystemClient jiraSystemClient;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private TeamMemberRepository teamMemberRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Value("${minio.bucket-name:test-bucket}")
    private String bucketName;
    
    private Project testProject;
    private Team testTeam;
    private TeamMember testTeamMember;
    private Resource testResource;
    private MockMultipartFile testFile;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
       
        testProject = Project.builder()
                .name("Test Project")
                .storageSize(BigInteger.ZERO)
                .maxStorageSize(BigInteger.valueOf(2_147_483_648L)) // 2GB
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        testProject = projectRepository.save(testProject);
        
        testTeam = Team.builder()
                .project(testProject)
                .build();
        testTeam = teamRepository.save(testTeam);
       
        testTeamMember = TeamMember.builder()
                .userId(100L)
                .nickname("Test User")
                .team(testTeam)
                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER, TeamRole.MANAGER)))
                .build();
        testTeamMember = teamMemberRepository.save(testTeamMember);
        
        testResource = Resource.builder()
                .name("test-file.pdf")
                .key("project-1/test-key.pdf")
                .size(BigInteger.valueOf(1024L))
                .contentType("application/pdf")
                .type(ResourceType.PDF)
                .status(ResourceStatus.ACTIVE)
                .project(testProject)
                .createdBy(testTeamMember)
                .updatedBy(testTeamMember)
                .allowedRoles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .build();
        testResource = resourceRepository.save(testResource);
        
        testFile = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "Test PDF content for integration test".getBytes()
        );
        
        // Mock MinIO operations
        try {
            doAnswer(invocation -> null).when(minioClient).putObject(any(PutObjectArgs.class));
        } catch (Exception e) {
            // Ignore
        }
    }
    
    @Test
    @DisplayName("Should upload file successfully")
    void shouldUploadFileSuccessfully() throws Exception {
        // When & Then
        MvcResult result = mockMvc.perform(
                multipart("/api/v1/projects/{projectId}/resources", testProject.getId())
                        .file(testFile)
                        .param("allowedRoles", "DEVELOPER")
                        .header("x-team-member-id", testTeamMember.getId())
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test-document.pdf"))
                .andReturn();
        
        // Verify database state
        String responseContent = result.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseContent);
        Long resourceId = responseJson.get("id").asLong();
        
        Optional<Resource> savedResource = resourceRepository.findById(resourceId);
        assertTrue(savedResource.isPresent());
        assertEquals("test-document.pdf", savedResource.get().getName());
        assertEquals(ResourceStatus.ACTIVE, savedResource.get().getStatus());
        
        // Verify MinIO interaction
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }
    
    @Test
    @DisplayName("Should return 413 when file too large")
    void shouldReturn413WhenFileTooLarge() throws Exception {
        // Given
        long oversizedBytes = 501L * 1024 * 1024; // 501MB
        MockMultipartFile largeFile = new OversizedMockMultipartFile(
                "file",
                "large.zip",
                "application/zip",
                "placeholder".getBytes(),
                oversizedBytes
        );
        
        // When & Then
        mockMvc.perform(
                multipart("/api/v1/projects/{projectId}/resources", testProject.getId())
                        .file(largeFile)
                        .header("x-team-member-id", testTeamMember.getId())
        )
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.errorCode").value("413 PAYLOAD_TOO_LARGE"));
    }
    
    @Test
    @DisplayName("Should download file successfully")
    void shouldDownloadFileSuccessfully() throws Exception {
        // Given
        InputStream mockInputStream = new ByteArrayInputStream("file content".getBytes());
        GetObjectResponse mockResponse = mock(GetObjectResponse.class);
        
        doAnswer(invocation -> mockResponse).when(minioClient).getObject(any(GetObjectArgs.class));
        when(mockResponse.readAllBytes()).thenReturn("file content".getBytes());
        
        // When & Then
        mockMvc.perform(
                get("/api/v1/projects/{projectId}/resources/{resourceId}/download",
                        testProject.getId(), testResource.getId())
                        .header("x-team-member-id", testTeamMember.getId())
        )
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        containsString("attachment")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        containsString("test-file.pdf")));
        
        verify(minioClient).getObject(any(GetObjectArgs.class));
    }
    
    @Test
    @DisplayName("Should delete file successfully")
    void shouldDeleteFileSuccessfully() throws Exception {
        // Given
        try {
            doAnswer(invocation -> null).when(minioClient).removeObject(any(RemoveObjectArgs.class));
        } catch (Exception e) {
            // Ignore
        }
        
        // When
        mockMvc.perform(
                delete("/api/v1/projects/{projectId}/resources/{resourceId}",
                        testProject.getId(), testResource.getId())
                        .header("x-team-member-id", testTeamMember.getId())
        )
                .andExpect(status().isNoContent());
        
        // Then
        Optional<Resource> deletedResource = resourceRepository.findById(testResource.getId());
        assertTrue(deletedResource.isPresent());
        assertEquals(ResourceStatus.DELETED, deletedResource.get().getStatus());
        assertNull(deletedResource.get().getKey());
        assertEquals(BigInteger.ZERO, deletedResource.get().getSize());
        
        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }
    
    @Test
    @DisplayName("Should return 403 when user lacks permission")
    void shouldReturn403WhenUserLacksPermission() throws Exception {
        // Given - Create unauthorized team member
        TeamMember unauthorizedMember = TeamMember.builder()
                .userId(200L)
                .nickname("Unauthorized")
                .team(testTeam)
                .roles(new ArrayList<>(List.of(TeamRole.TESTER)))
                .build();
        unauthorizedMember = teamMemberRepository.save(unauthorizedMember);
        
        // When & Then - Exception should be handled and return 403 Forbidden
        mockMvc.perform(
                delete("/api/v1/projects/{projectId}/resources/{resourceId}",
                        testProject.getId(), testResource.getId())
                        .header("x-team-member-id", unauthorizedMember.getId())
        )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").value(
                        containsString("Only file creator or project manager can delete files")));
    }
    
    @Test
    @DisplayName("Should get project files with pagination")
    void shouldGetProjectFilesWithPagination() throws Exception {
        // When & Then
        mockMvc.perform(
                get("/api/v1/projects/{projectId}/resources", testProject.getId())
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                        .header("x-team-member-id", testTeamMember.getId())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10));
    }
    
    @Test
    @DisplayName("Should upload multiple files successfully")
    void shouldUploadMultipleFilesSuccessfully() throws Exception {
        // Given
        MockMultipartFile file1 = new MockMultipartFile(
                "files", "doc1.pdf", "application/pdf", "content1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "files", "doc2.docx", 
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", 
                "content2".getBytes()
        );
        
        // When & Then
        mockMvc.perform(
                multipart("/api/v1/projects/{projectId}/resources/bulk", testProject.getId())
                        .file(file1)
                        .file(file2)
                        .header("x-team-member-id", testTeamMember.getId())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("doc1.pdf"))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$[1].name").value("doc2.docx"))
                .andExpect(jsonPath("$[1].status").value("SUCCESS"));
        
        verify(minioClient, atLeast(1)).putObject(any(PutObjectArgs.class));
    }
    
    @Test
    @DisplayName("Should generate presigned URL")
    void shouldGeneratePresignedUrl() throws Exception {
        // Given
        String expectedUrl = "https://minio.example.com/bucket/file?X-Amz-Signature=abc123";
        doAnswer(invocation -> expectedUrl)
                .when(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
        
        // When & Then
        mockMvc.perform(
                get("/api/v1/projects/{projectId}/resources/{resourceId}/url",
                        testProject.getId(), testResource.getId())
                        .header("x-team-member-id", testTeamMember.getId())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(expectedUrl))
                .andExpect(jsonPath("$.expiresIn").value(3600));
        
        verify(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }
    
    @Test
    @DisplayName("Should return 404 when resource not found")
    void shouldReturn404WhenResourceNotFound() throws Exception {
        // When & Then
        mockMvc.perform(
                get("/api/v1/projects/{projectId}/resources/{resourceId}/download",
                        testProject.getId(), 99999L)
                        .header("x-team-member-id", testTeamMember.getId())
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
    }

    private static class OversizedMockMultipartFile extends MockMultipartFile {

        private final long reportedSize;

        OversizedMockMultipartFile(String name, String originalFilename,
                                   String contentType, byte[] content, long reportedSize) {
            super(name, originalFilename, contentType, content);
            this.reportedSize = reportedSize;
        }

        @Override
        public long getSize() {
            return reportedSize;
        }
    }
}

