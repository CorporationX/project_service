package faang.school.projectservice.projectServiceTests.projectControllerTests;

import faang.school.projectservice.controller.projectController.ProjectController;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;


import static faang.school.projectservice.model.ProjectStatus.COMPLETED;
import static faang.school.projectservice.model.ProjectStatus.CREATED;
import static faang.school.projectservice.model.ProjectStatus.IN_PROGRESS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ContextConfiguration(classes = {ProjectController.class})
public class ProjectControllerTest {

    @MockBean
    private ProjectService projectService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void findAllProjectsTest() throws Exception {
        List<ProjectResponseDto> mockProjects = Arrays.asList(
                ProjectResponseDto
                        .builder()
                        .id(1L)
                        .status(CREATED)
                        .description("This my first TEST")
                        .build(),
                ProjectResponseDto
                        .builder()
                        .id(2L)
                        .status(COMPLETED)
                        .description("This my second TEST")
                        .build()
        );

        when(projectService.findAllProject()).thenReturn(mockProjects);

        mockMvc.perform(get("/projects/all"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description", is("This my first TEST")))
                .andExpect(jsonPath("$[1].description", is("This my second TEST")))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(CREATED))))
                .andExpect(jsonPath("$[1].status", is(String.valueOf(COMPLETED))));

        verify(projectService, times(1)).findAllProject();
    }

    @Test
    public void getProjectByIdTest() throws Exception {
        ProjectResponseDto projectResponseDto = ProjectResponseDto
                .builder()
                .id(1L)
                .name("Test project")
                .status(CREATED)
                .build();

        when(projectService.getProjectById(1L)).thenReturn(projectResponseDto);

        mockMvc.perform(get("/projects/get/{projectId}", 1L))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test project")))
                .andExpect(jsonPath("$.status", is(String.valueOf(CREATED))));

        verify(projectService, times(1)).getProjectById(1L);
    }

    @Test
    public void getAllProjectsWithFiltersTest() throws Exception {
        List<ProjectResponseDto> mockProjects = Collections.singletonList(
                ProjectResponseDto
                        .builder()
                        .id(1L)
                        .status(CREATED)
                        .description("This my first TEST")
                        .build()
        );

        ProjectFilterDto filterDto = ProjectFilterDto
                .builder()
                .name("This my first TEST")
                .status(COMPLETED)
                .build();

        when(projectService.findAllProjectsWithFilters(filterDto)).thenReturn(mockProjects);

        mockMvc.perform(post("/projects/filtered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filterDto)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(CREATED))))
                .andExpect(jsonPath("$[0].description", is("This my first TEST")));

        verify(projectService, times(1)).findAllProjectsWithFilters(filterDto);
    }

    @Test
    public void createProjectTest() throws Exception {
        ProjectCreateDto projectCreateDto = ProjectCreateDto
                .builder()
                .ownerId(1L)
                .description("Тут новый проект")
                .name("New project")
                .build();

        ProjectResponseDto projectResponseDto = ProjectResponseDto
                .builder()
                .ownerId(1L)
                .description("Тут новый проект")
                .name("New project")
                .build();

        when(projectService.createProject(projectCreateDto)).thenReturn(projectResponseDto);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(projectCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId", is(1)))
                .andExpect(jsonPath("$.description", is("Тут новый проект")))
                .andExpect(jsonPath("$.name", is("New project")));

        verify(projectService, times(1)).createProject(projectCreateDto);
    }

    @Test
    public void updateProjectTest() throws Exception {
        long projectId = 1L;
        ProjectResponseDto projectResponseDto = ProjectResponseDto
                .builder()
                .description("Тут новый проект")
                .status(IN_PROGRESS)
                .build();

        ProjectUpdateDto projectUpdateDto = ProjectUpdateDto
                .builder()
                .description("Old description")
                .status(CREATED)
                .build();

        when(projectService.updateProject(projectId, projectUpdateDto)).thenReturn(projectResponseDto);

        mockMvc.perform(put("/projects/{projectId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(projectUpdateDto)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description", is("Тут новый проект")))
                .andExpect(jsonPath("$.status", is(String.valueOf(IN_PROGRESS))));

        verify(projectService, times(1)).updateProject(projectId, projectUpdateDto);
    }
}
