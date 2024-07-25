package faang.school.projectservice.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.controller.resource.ResourceController;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigInteger;

import static faang.school.projectservice.resource.ResourceMock.fileSize;
import static faang.school.projectservice.resource.ResourceMock.generateMultipartFile;
import static faang.school.projectservice.resource.ResourceMock.generateResourceDto;
import static faang.school.projectservice.resource.ResourceMock.projectId;
import static faang.school.projectservice.resource.ResourceMock.resourceId;
import static faang.school.projectservice.resource.ResourceMock.resourceName;
import static faang.school.projectservice.resource.ResourceMock.resourceUrl;
import static faang.school.projectservice.resource.ResourceMock.userId;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ResourceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ResourceService service;

    @InjectMocks
    private ResourceController controller;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testGet() throws Exception {
        // Arrange
        when(service.getResource(resourceId)).thenReturn(resourceUrl);

        // Act & Assert
        mockMvc.perform(get("/resources/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(resourceUrl)));
    }

    @Test
    public void testCreate() throws Exception {
        // Arrange
        ResourceDto resourceDto = generateResourceDto();

        MockMultipartFile file = generateMultipartFile();
        when(service.createResource(userId, projectId, file)).thenReturn(resourceDto);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.multipart("/resources")
                        .file(file)
                        .header("x-user-id", String.valueOf(userId))
                        .param("projectId", String.valueOf(projectId))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) resourceId)))
                .andExpect(jsonPath("$.size", is(fileSize.intValue())))
                .andExpect(jsonPath("$.name", is(resourceName)));
    }

    @Test
    public void testUpdate() throws Exception {
        // Arrange
        String newFileName = "changedAvatar.jpg";
        BigInteger newFileSize = BigInteger.valueOf(20000);
        ResourceDto resourceDto = generateResourceDto(resourceId, newFileName, newFileSize, projectId);

        MockMultipartFile file = generateMultipartFile(newFileName, newFileSize);
        when(service.updateResource(userId, resourceId, file)).thenReturn(resourceDto);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.multipart("/resources/1")
                        .file(file)
                        .header("x-user-id", String.valueOf(userId))
                        .with(new RequestPostProcessor() {
                            @Override
                            public @NotNull MockHttpServletRequest postProcessRequest(@NotNull MockHttpServletRequest request) {
                                request.setMethod("PATCH");
                                return request;
                            }
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) resourceId)))
                .andExpect(jsonPath("$.size", is(newFileSize.intValue())))
                .andExpect(jsonPath("$.name", is(newFileName)));
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/resources/1")
                        .header("x-user-id", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}