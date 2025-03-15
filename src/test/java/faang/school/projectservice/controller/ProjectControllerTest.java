package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exceptionhandler.GlobalExceptionHandler;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.ProjectCoverService;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static faang.school.projectservice.constant.ImageTestConstants.IMAGE_MOCK_MULTIPART_FILE;
import static faang.school.projectservice.constant.ProjectTestConstants.PROJECT_COVER_IMAGE_ID;
import static faang.school.projectservice.constant.ProjectTestConstants.TEST_PROJECT_ID;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectCoverService projectCoverService;

    @InjectMocks
    private ProjectController projectController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void testSuccessCreateProject() throws Exception {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Chairs hand made");
        projectDto.setDescription("Some description");
        projectDto.setStatus(ProjectStatus.CREATED);

        ArgumentCaptor<ProjectDto> captor = ArgumentCaptor.forClass(ProjectDto.class);

        Mockito.when(projectService.createProject(Mockito.any(ProjectDto.class))).thenReturn(projectDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Chairs hand made"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Some description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CREATED"));

        Mockito.verify(projectService, Mockito.times(1)).createProject(captor.capture());

        ProjectDto result = captor.getValue();
        Assertions.assertEquals("Chairs hand made", result.getName());
        Assertions.assertEquals("Some description", result.getDescription());
        Assertions.assertEquals(ProjectStatus.CREATED, result.getStatus());
    }

    @Test
    void testCreateProjectWithoutName() throws Exception {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName(" ");
        projectDto.setDescription("Some description");
        projectDto.setStatus(ProjectStatus.CREATED);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("name must be fielded"));
    }

    @Test
    void testCreateProjectWithoutDescription() throws Exception {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Project name");
        projectDto.setDescription(" ");
        projectDto.setStatus(ProjectStatus.CREATED);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("description must be fielded"));
    }

    @Test
    void testUpdateProjectSuccess() throws Exception {
        ProjectDto updatedProjectDto = new ProjectDto();
        updatedProjectDto.setName("Project name");
        updatedProjectDto.setDescription("Project description");
        updatedProjectDto.setStatus(ProjectStatus.ON_HOLD);

        Mockito.when(projectService.updateProject(Mockito.any(ProjectDto.class))).thenReturn(updatedProjectDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProjectDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Project name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Project description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ON_HOLD"));

        Mockito.verify(projectService, Mockito.times(1)).updateProject(updatedProjectDto);
    }

    @Test
    void getProjectsWithFilter() throws Exception {
        long userId = 10L;

        ProjectFilterDto nameFilter = new ProjectFilterDto();
        nameFilter.setNamePattern("chairs");

        ProjectDto dto = new ProjectDto();
        dto.setName("Project name");
        dto.setDescription("Some description");

        Mockito.when(projectService.getAllAvailableProjectsForUserWithFilter(nameFilter, userId)).thenReturn(List.of(dto));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/project/user/{userId}/filter", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nameFilter)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Project name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Some description"));

        Mockito.verify(projectService, Mockito.times(1)).getAllAvailableProjectsForUserWithFilter(nameFilter, userId);
    }

    @Test
    void testGetAllAvailableProjectsForUser() throws Exception {
        long userId = 1L;

        ProjectDto dto = new ProjectDto();
        dto.setName("Project name");
        dto.setDescription("Some description");

        Mockito.when(projectService.getAllAvailableProjectsForUser(userId)).thenReturn(List.of(dto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/project/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Project name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Some description"));

        Mockito.verify(projectService, Mockito.times(1)).getAllAvailableProjectsForUser(userId);
    }

    @Test
    void testGetProjectById() throws Exception {
        long projectId = 20L;

        ProjectDto dto = new ProjectDto();
        dto.setId(projectId);
        dto.setName("Project name");

        Mockito.when(projectService.getProjectById(projectId)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(20))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Project name"));
    }

    @Test
    void addProjectCover_shouldBeCompletedSuccessfully() throws Exception {

        Mockito.when(projectCoverService.addProjectCover(TEST_PROJECT_ID, IMAGE_MOCK_MULTIPART_FILE))
                .thenReturn(PROJECT_COVER_IMAGE_ID);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/project/{id}/cover",
                                TEST_PROJECT_ID)
                        .file(IMAGE_MOCK_MULTIPART_FILE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(PROJECT_COVER_IMAGE_ID));

        Mockito.verify(projectCoverService, Mockito.times(1))
                .addProjectCover(TEST_PROJECT_ID, IMAGE_MOCK_MULTIPART_FILE);
    }

    @Test
    void deleteProjectCover_shouldBeCompletedSuccessfully() throws Exception {

        Mockito.when(projectCoverService.deleteProjectCover(TEST_PROJECT_ID)).thenReturn(PROJECT_COVER_IMAGE_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/project/{id}/cover", TEST_PROJECT_ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(PROJECT_COVER_IMAGE_ID));

        Mockito.verify(projectCoverService, Mockito.times(1)).deleteProjectCover(TEST_PROJECT_ID);
    }

    @Test
    void getProjectCover_shouldBeCompletedSuccessfully() throws Exception {
        byte[] projectCoverData = PROJECT_COVER_IMAGE_ID.getBytes();
        InputStream projectCoverInputStream = new ByteArrayInputStream(projectCoverData);

        Mockito.when(projectCoverService.getProjectCover(TEST_PROJECT_ID)).thenReturn(projectCoverInputStream);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/project/{id}/cover", TEST_PROJECT_ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(projectCoverData));

        Mockito.verify(projectCoverService, Mockito.times(1)).getProjectCover(TEST_PROJECT_ID);
    }
}
