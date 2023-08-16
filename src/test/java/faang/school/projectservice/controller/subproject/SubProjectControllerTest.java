package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.StatusSubprojectDto;
import faang.school.projectservice.dto.subproject.VisibilitySubprojectUpdateDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private StatusSubprojectDto statusSubprojectDto = StatusSubprojectDto.builder().build();
    private VisibilitySubprojectUpdateDto visibilitySubprojectUpdateDto = VisibilitySubprojectUpdateDto.builder().build();
    private SubProjectDto subProjectDto = SubProjectDto.builder().build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(subProjectController).build();
    }

    @Test
    void testCreateSubProject() throws Exception {
        Mockito.when(subProjectService.createProject(subProjectDto)).thenReturn(subProjectDto);
        mockMvc.perform(post("/subproject/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subProjectDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(subProjectDto)));

        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateCreateProjectDto(subProjectDto);
        Mockito.verify(subProjectService, Mockito.times(1))
                .createProject(subProjectDto);
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
                        .content(objectMapper.writeValueAsString(visibilitySubprojectUpdateDto)))
                .andExpect(status().isOk());

        Mockito.verify(subProjectService, Mockito.times(1))
                .updateVisibilitySubProject(visibilitySubprojectUpdateDto);
    }
}