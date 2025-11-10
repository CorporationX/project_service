package faang.school.project_service.controller.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.controller.project.ProjectController;
import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.project.ProjectServiceImpl;
import faang.school.projectservice.service.resource.ResourceService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String basePath = "/projects";
    private MockMvc mockMvc;

    @Mock
    private ProjectServiceImpl projectService;
    @Mock
    private ResourceService resourceService;
    @InjectMocks
    private ProjectController projectController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
    }

    @Test
    void testCreate() throws Exception {
        CreateProjectDto createProjectDto = CreateProjectDto.builder()
                .name("New project")
                .description("New desc")
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        ProjectDto projectDto = ProjectDto.builder()
                .id(1L)
                .name(createProjectDto.name())
                .description(createProjectDto.description())
                .build();

        Mockito.when(projectService.create(createProjectDto)).thenReturn(projectDto);

        mockMvc.perform(MockMvcRequestBuilders.post(basePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProjectDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(ProjectDto.Fields.name, Matchers.equalTo(projectDto.name())))
                .andExpect(jsonPath(ProjectDto.Fields.description, Matchers.equalTo(projectDto.description())))
                .andExpect(jsonPath(ProjectDto.Fields.id, Matchers.equalTo(projectDto.id().intValue())));
    }

    @Test
    void testUpdate() throws Exception {
        UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
                .status(ProjectStatus.ON_HOLD)
                .description("Some desc")
                .build();

        ProjectDto projectDto = ProjectDto.builder()
                .id(1L)
                .description(updateProjectDto.description())
                .status(updateProjectDto.status())
                .build();

        Mockito.when(projectService.update(projectDto.id(), updateProjectDto)).thenReturn(projectDto);

        mockMvc.perform(MockMvcRequestBuilders.patch(basePath + "/{projectId}", projectDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProjectDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ProjectDto.Fields.description, Matchers.equalTo(projectDto.description())))
                .andExpect(jsonPath(ProjectDto.Fields.status, Matchers.equalTo(projectDto.status().toString())))
                .andExpect(jsonPath(ProjectDto.Fields.id, Matchers.equalTo(projectDto.id().intValue())));
    }

    @Test
    void testGetProjectByFilters() throws Exception {
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder()
                .status(ProjectStatus.CREATED)
                .name("Good project")
                .build();

        ProjectDto projectDtoOne = ProjectDto.builder()
                .name(projectFilterDto.name())
                .status(projectFilterDto.status())
                .id(1L)
                .build();

        ProjectDto projectDtoTwo = ProjectDto.builder()
                .name(projectFilterDto.name())
                .status(projectFilterDto.status())
                .id(2L)
                .build();

        List<ProjectDto> expectedProjectsDto = new ArrayList<>(List.of(projectDtoOne, projectDtoTwo));

        when(projectService.getByFilters(projectFilterDto)).thenReturn(expectedProjectsDto);

        String response = mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/filter")
                        .param(ProjectFilterDto.Fields.name, projectFilterDto.name())
                        .param(ProjectFilterDto.Fields.status, String.valueOf(projectFilterDto.status())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(expectedProjectsDto.size())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ProjectDto> actualProjects = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ProjectDto.class));

        Assertions.assertTrue(actualProjects.stream().map(ProjectDto::id).toList()
                .containsAll(expectedProjectsDto.stream().map(ProjectDto::id).toList()));
    }

    @Test
    void testGetProjectById() throws Exception {
        ProjectDto projectDto = ProjectDto.builder()
                .id(1L)
                .name("Name for name")
                .status(ProjectStatus.CREATED)
                .build();

        when(projectService.getProjectById(projectDto.id())).thenReturn(projectDto);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/{projectId}", projectDto.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ProjectDto.Fields.name, Matchers.equalTo(projectDto.name())))
                .andExpect(jsonPath(ProjectDto.Fields.status, Matchers.equalTo(projectDto.status().toString())))
                .andExpect(jsonPath(ProjectDto.Fields.id, Matchers.equalTo(projectDto.id().intValue())));
    }

    @Test
    void testGetAllProjects() throws Exception {
        ProjectDto projectDtoOne = ProjectDto.builder()
                .id(1L)
                .name("First name")
                .status(ProjectStatus.CREATED)
                .build();

        ProjectDto projectDtoTwo = ProjectDto.builder()
                .id(2L)
                .name("Second name")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        List<ProjectDto> expectedProjects = new ArrayList<>(List.of(projectDtoOne, projectDtoTwo));

        when(projectService.getAllProjects()).thenReturn(expectedProjects);

        String response = mockMvc.perform(MockMvcRequestBuilders.get(basePath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(expectedProjects.size())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ProjectDto> actualProjects = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ProjectDto.class));

        Assertions.assertTrue(actualProjects.stream().map(ProjectDto::id).toList()
                .containsAll(expectedProjects.stream().map(ProjectDto::id).toList()));
        Assertions.assertTrue(actualProjects.stream().map(ProjectDto::name).toList()
                .containsAll(expectedProjects.stream().map(ProjectDto::name).toList()));
    }

    @Test
    void testAddProjectAvatar() throws Exception {
        ResourceDto resourceDto = ResourceDto.builder()
                .id(134L)
                .build();
        long projectId = 1L;
        MockMultipartFile file
                = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[]{});

        when(resourceService.addProjectAvatar(Mockito.eq(projectId), Mockito.eq(file))).thenReturn(resourceDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart(basePath + "/{projectId}/avatar", projectId)
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(ResourceDto.Fields.id, Matchers.equalTo(resourceDto.id().intValue())));
    }
}