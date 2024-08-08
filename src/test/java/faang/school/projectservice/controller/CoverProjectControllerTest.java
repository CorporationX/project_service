package faang.school.projectservice.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CoverProjectControllerTest {
    public static final long MAX_IMAGE_SIZE = 5242880L;

    private static final String MESSAGE_INVALID_PROJECT_ID = "Invalid projectId";
    private static final long VALID_ID = 1L;
    private static final String RANDOM_KEY = "key";
    private MockMvc mockMvc;
    private ObjectWriter objectWriter;
    private MultipartFile cover;
    private ProjectCoverDto dto;
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
        ObjectMapper objectMapper = new ObjectMapper();
        objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    public void testServiceAddPostLike() throws Exception {
        //Act
        MockMultipartFile mockCover = new MockMultipartFile("cover", "hey".getBytes());
        //Assert
        mockMvc.perform(post("/api/project/1/cover")
                        .contentType(MediaType.MULTIPART_FORM_DATA).content(mockCover.getBytes()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_ID))
                .andExpect(jsonPath("$.userId").value(VALID_ID));
    }
}