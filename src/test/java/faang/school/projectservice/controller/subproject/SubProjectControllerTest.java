package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.dto.subproject.VisibilitySubprojectDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SubProjectControllerTest {
    @InjectMocks
    private SubProjectController subProjectController;
    @Mock
    private SubProjectService subProjectService;
    @Mock
    private SubProjectValidator subProjectValidator;
    @Spy
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private SubprojectFilterDto subprojectFilterDto = SubprojectFilterDto.builder().build();
    private VisibilitySubprojectDto visibilitySubprojectDto = VisibilitySubprojectDto.builder().build();
    private SubProjectDto subProjectDto = SubProjectDto.builder().build();
    private ProjectDto projectDto = ProjectDto.builder().build();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(subProjectController).build();
    }

    @Test
    void testCreateSubProject() throws Exception {
        mockMvc.perform(post("/subproject/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllSubProjects() throws Exception {
        mockMvc.perform(get("/subproject/filter/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subprojectFilterDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateSubProjectStatus() throws Exception {
        mockMvc.perform(put("/subproject/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusSubprojectDto)))
                .andExpect(status().isOk());

        Mockito.verify(subProjectService, Mockito.times(1))
                .updateStatusSubProject(statusSubprojectDto);
    }

    @Test
    void testCreateSubProjectVisibility() throws Exception {
        mockMvc.perform(put("/subproject/visibility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(visibilitySubprojectDto)))
                .andExpect(status().isOk());

        Mockito.verify(subProjectService, Mockito.times(1))
                .updateVisibilitySubProject(visibilitySubprojectDto);
    }
}