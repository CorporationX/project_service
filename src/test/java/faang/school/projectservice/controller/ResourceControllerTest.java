package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ResourceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ResourceService resourceService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ResourceController resourceController;

    @Spy
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceController).build();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testUploadFile() throws Exception {
        MockMultipartFile resourceDtoJson = new MockMultipartFile("resourceDto", "", "application/json",
                "{\"projectId\": \"1\"}".getBytes());

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
        mockMvc.perform(multipart("/resources/upload")
                        .file(file)
                        .file(resourceDtoJson))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateFile() throws Exception {
        MockMultipartFile resourceDtoJson = new MockMultipartFile("resourceDto", "", "application/json",
                ("{" +
                        "\"id\": 1," +
                        "\"projectId\": 1" +
                        "}").getBytes());
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());

        MockMultipartHttpServletRequestBuilder multipart = MockMvcRequestBuilders.multipart("/resources/upload/1")
                .file(file)
                .file(resourceDtoJson);

        multipart.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(multipart)
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteResource() throws Exception {
        mockMvc.perform(delete("/resources/1"))
                .andExpect(status().isOk());
    }
}