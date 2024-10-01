package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(SubProjectController.class)
class SubProjectControllerTest {
    long projectId = 2L;
    long parentId = 2L;

    private CreateSubProjectDto createSubProjectDto;
    private ProjectDto projectDto;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserContext userContext;

    @MockBean
    private ProjectService projectService;

    @BeforeEach
    void setUp() {

        createSubProjectDto = new CreateSubProjectDto(null, "Test subproject", "Description", parentId);
        projectDto = ProjectDto.builder().name("Test").build();

    }

    @Test
    void createSubProject() throws Exception {
        Long userId = 1L;

        when(userContext.getUserId()).thenReturn(userId);
        when(projectService.createSubProject(eq(userId), any(CreateSubProjectDto.class))).thenReturn(projectDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1//project/subproject/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSubProjectDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(projectDto.name()));
    }

    @Test
    void updateSubProject() throws Exception {
        Long userId = 1L;
        UpdateSubProjectDto updateSubProjectDto = UpdateSubProjectDto.builder().projectId(projectId).build();
        when(userContext.getUserId()).thenReturn(userId);
        when(projectService.updateSubProject(eq(userId), any(UpdateSubProjectDto.class))).thenReturn(projectDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1//project/subproject/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateSubProjectDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(projectDto.name()));
    }

    @Test
    void getSubProjects() throws Exception {
        FilterSubProjectDto filter = new FilterSubProjectDto(null, null);
        List<ProjectDto> projectDtos = List.of(projectDto, ProjectDto.builder().name("Test2").build());
        when(projectService.getSubProjects(anyLong(), any(FilterSubProjectDto.class), anyInt(), anyInt()))
                .thenReturn(projectDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1//project/subproject/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "10")
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(projectDtos.size()));
    }
}