package faang.school.projectservice.controller;

import faang.school.projectservice.AbstractIntegrationTest;
import faang.school.projectservice.repository.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.GlobalExceptionHandler;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static faang.school.projectservice.constant.ImageTestConstants.IMAGE_MOCK_MULTIPART_FILE;
import static faang.school.projectservice.constant.ProjectTestConstants.NOT_OWNER_ID;
import static faang.school.projectservice.constant.ProjectTestConstants.OWNER_ID;
import static faang.school.projectservice.constant.ProjectTestConstants.PROJECT_COVER_IMAGE_ID;

public class ProjectControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectController projectController;

    @Autowired
    private ProjectRepositoryAdapter projectRepositoryAdapter;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserContext userContext;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private long testProjectId;

    ProjectDto testProjectDto = new ProjectDto();

    @BeforeEach
    void setUp() {
        userContext.setUserId(OWNER_ID);

        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        Project testProject = Project.builder()
                .name("Test")
                .description("Test project")
                .ownerId(OWNER_ID)
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CREATED)
                .build();

        testProjectId = projectRepository.save(testProject).getId();

        testProjectDto = ProjectDto.builder()
                .name("Test")
                .description("Test project")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    @Test
    void testSuccessCreateProject() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProjectDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Test project"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CREATED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.visibility").value("PUBLIC"));
    }

    @Test
    void testCreateProjectWithoutName() throws Exception {
        testProjectDto.setName("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProjectDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.name")
                        .value("name must be fielded"));
    }

    @Test
    void testCreateProjectWithoutDescription() throws Exception {
        testProjectDto.setDescription("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProjectDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.description")
                        .value("description must be fielded"));
    }

    @Test
    void testUpdateProjectSuccess() throws Exception {
        ProjectDto updatedProjectDto = new ProjectDto();
        updatedProjectDto.setId(testProjectId);
        updatedProjectDto.setName("Project name");
        updatedProjectDto.setDescription("Project description");
        updatedProjectDto.setStatus(ProjectStatus.ON_HOLD);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProjectDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Project name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Project description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ON_HOLD"));
    }

    @Test
    void getProjectsWithFilter() throws Exception {
        ProjectFilterDto nameFilter = new ProjectFilterDto();
        nameFilter.setNamePattern("Test");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/projects/user/{userId}/filter", OWNER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nameFilter)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Test project"));
    }

    @Test
    void testGetAllAvailableProjectsForUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/projects/user/{userId}", OWNER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("AI Research"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description")
                        .value("Project for AI development"));
    }

    @Test
    void testGetProjectById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/projects/{id}", testProjectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testProjectId));
    }

    @Test
    void addProjectCover_shouldThrowBadRequestException_whenTheUserIsNotTheAuthorOfTheProject() throws Exception {
        userContext.setUserId(NOT_OWNER_ID);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/projects/{id}/cover", testProjectId)
                        .file(IMAGE_MOCK_MULTIPART_FILE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Only the owner of the project can add a cover"));
    }

    @Test
    void addProjectCover_shouldThrowBadRequestException_whenTheProjectAlreadyContainsCover() throws Exception {
        long testProjectId = projectRepository.save(
                Project.builder()
                        .name("Test")
                        .description("Test project with cover")
                        .ownerId(OWNER_ID)
                        .visibility(ProjectVisibility.PUBLIC)
                        .coverImageId(PROJECT_COVER_IMAGE_ID)
                        .status(ProjectStatus.CREATED)
                        .build()).getId();

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/projects/{id}/cover", testProjectId)
                        .file(IMAGE_MOCK_MULTIPART_FILE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("The project with ID " + testProjectId + " already has a cover"));
    }

    @Test
    void addProjectCover_shouldBeCompletedSuccessfully() throws Exception {
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.multipart("/api/v1/projects/{projectId}/cover", testProjectId)
                        .file(IMAGE_MOCK_MULTIPART_FILE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String key = result.getResponse().getContentAsString();
        Project updatedTestProject = projectRepositoryAdapter.getById(testProjectId);

        Assertions.assertTrue(isObjectExist(key));
        Assertions.assertNotNull(updatedTestProject.getCoverImageId());
    }

    @Test
    void deleteProjectCover_shouldThrowBadRequestException_whenTheUserIsNotTheAuthorOfTheProject() throws Exception {
        userContext.setUserId(NOT_OWNER_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/projects/{id}/cover", testProjectId))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Only the owner of the project can delete a cover"));
    }

    @Test
    void deleteProjectCover_shouldThrowBadRequestException_whenTheProjectDoesNotContainCover() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/projects/{id}/cover", testProjectId))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("The project with ID " + testProjectId + " does not have a cover"));
    }

    @Test
    void deleteProjectCover_shouldBeCompletedSuccessfully() throws Exception {
        addTestProjectCover();

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/v1/projects/{id}/cover", testProjectId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String key = result.getResponse().getContentAsString();
        Project updatedTestProject = projectRepositoryAdapter.getById(testProjectId);

        Assertions.assertFalse(isObjectExist(key));
        Assertions.assertNull(updatedTestProject.getCoverImageId());
    }

    @Test
    void getProjectCover_shouldBeCompletedSuccessfully() throws Exception {
        addTestProjectCover();

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/projects/{id}/cover", testProjectId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andReturn();

        byte[] key = result.getResponse().getContentAsByteArray();
        Project updatedTestProject = projectRepositoryAdapter.getById(testProjectId);

        Assertions.assertNotNull(key);
        Assertions.assertNotNull(updatedTestProject.getCoverImageId());
    }

    private void addTestProjectCover() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/projects/{id}/cover", testProjectId)
                        .file(IMAGE_MOCK_MULTIPART_FILE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private boolean isObjectExist(String key) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(MINIO_BUCKET)
                    .object(key)
                    .build());
            return true;
        } catch (ErrorResponseException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
