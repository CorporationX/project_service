package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CoverFromStorageDto;
import faang.school.projectservice.dto.ProjectCoverDto;
import faang.school.projectservice.service.CoverProjectService;
import faang.school.projectservice.validator.ControllerValidator;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CoverProjectControllerTest {
    private static final long VALID_ID = 1L;
    private static final String KEY = "1";
    private static final String TEST_STRING = "cover";
    private MockMvc mockMvc;
    private MockMultipartFile mockCover;

    @Mock
    private ControllerValidator validator;
    @Mock
    private CoverProjectService service;
    @InjectMocks
    private CoverProjectController controller;

    @BeforeEach
    public void setUp() {
        //Arrange
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        mockCover = new MockMultipartFile(TEST_STRING, TEST_STRING.getBytes());
    }

    @Test
    public void testUploadOrChangeProjectCover() throws Exception {
        //Act
        Mockito.when(service.uploadProjectCover(Mockito.anyLong(), Mockito.any()))
                .thenReturn(new ProjectCoverDto(VALID_ID, KEY));
        //Assert
        mockMvc.perform(multipart("/api/project/1/cover")
                        .file(mockCover)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(VALID_ID))
                .andExpect(jsonPath("$.coverId").value(KEY));
    }

    @Test
    public void testGetProjectCover() throws Exception {
        //Act
        Mockito.when(service.getProjectCover(Mockito.anyLong()))
                .thenReturn(new CoverFromStorageDto(mockCover.getBytes(), mockCover.getContentType()));
        //Assert
        mockMvc.perform(get("/api/project/1/cover")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().bytes(mockCover.getBytes()));
    }

    @Test
    public void testDeleteProjectCover() throws Exception {
        //Assert
        mockMvc.perform(delete("/api/project/1/cover")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}