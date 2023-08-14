package faang.school.projectservice.controller.subproject;

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
    private VisibilitySubprojectUpdateDto visibilitySubprojectUpdateDto = VisibilitySubprojectUpdateDto.builder().build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(subProjectController).build();
    }

    @Test
    void testCreateSubProject() throws Exception {
        mockMvc.perform(put("/subproject/visibility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(visibilitySubprojectUpdateDto)))
                .andExpect(status().isOk());

        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateUpdateVisibilitySubprojectDto(visibilitySubprojectUpdateDto);
        Mockito.verify(subProjectService, Mockito.times(1))
                .updateVisibilitySubProject(visibilitySubprojectUpdateDto);
    }
}