package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.exception.GlobalExceptionHandler;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigInteger;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ContextConfiguration(classes = {ResourceController.class})
class ResourceControllerTest {

    private static final String BASE_URL = "/resources";
    private static final String UPLOAD_SINGLE_RESOURCE_URL = "/{projectId}";
    private static final String DELETE_URL = "/{projectId}/{resourceId}";
    private static final String UPDATE_URL = "/{projectId}/{resourceId}";
    private static final String GET_URL = "/{projectId}/{resourceId}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceService resourceService;

    ResourceResponseDto resourceResponseDto;
    MockMultipartFile file;
    Long projectId;
    Long resourceId;
    Long userId;
    @Autowired
    private ResourceController resourceController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        resourceResponseDto = mockResourceResponseDto();
        file = mockMultipartFile();
        projectId = 1L;
        userId = 2L;
        resourceId = 3L;
    }

    @Test
    @DisplayName("File upload success")
    void uploadResource_ValidFileAndParams_Success() throws Exception {
        when(resourceService.uploadResource(projectId, userId, file)).thenReturn(resourceResponseDto);

        mockMvc.perform(multipart(BASE_URL + UPLOAD_SINGLE_RESOURCE_URL, projectId)
                        .file(file)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value( resourceResponseDto.getId()))
                .andExpect(jsonPath("$.message").value(String.format("File %s uploaded successfully", resourceResponseDto.getName())));
    }

    @Test
    @DisplayName("File upload fail: negative project id")
    void uploadResource_NegativeProjectId_FailBadRequest() throws Exception {
        projectId = -1L;

        mockMvc.perform(multipart(BASE_URL + UPLOAD_SINGLE_RESOURCE_URL, projectId)
                        .file(file)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Project id must be a positive integer")));
    }

    @Test
    @DisplayName("File upload fail: null project id")
    void uploadResource_NullProjectId_FailNotFound() throws Exception {
        projectId = null;

        mockMvc.perform(multipart(BASE_URL + UPLOAD_SINGLE_RESOURCE_URL, projectId)
                        .file(file)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("File upload fail: user id not provided")
    void uploadResource_UserIdNotProvided_FailBadRequest() throws Exception {

        mockMvc.perform(multipart(BASE_URL + UPLOAD_SINGLE_RESOURCE_URL, projectId)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userId").value("Parameter cannot be null"))
                .andExpect(content().string(containsString("Parameter cannot be null")));
    }

    @Test
    @DisplayName("File upload fail: negative user id")
    void uploadResource_NegativeUserId_FailBadRequest() throws Exception {
        userId = -1L;

        mockMvc.perform(multipart(BASE_URL + UPLOAD_SINGLE_RESOURCE_URL, projectId)
                        .file(file)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User id must be a positive integer")));
    }

    @Test
    @DisplayName("File upload fail: file not provided")
    void uploadResource_FileNotProvided_FailBadRequest() throws Exception {

        mockMvc.perform(multipart(BASE_URL + UPLOAD_SINGLE_RESOURCE_URL, projectId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.file").value("Value not provided"))
                .andExpect(content().string(containsString("Value not provided")));
    }

    @Test
    @DisplayName("File delete success")
    void deleteResource_ValidFileAndParams_Success() throws Exception {

        mockMvc.perform(delete(BASE_URL + DELETE_URL, projectId, resourceId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(String.format("File id: %d was deleted successfully", resourceId)));
    }

    @Test
    @DisplayName("File delete fail: negative projectId")
    void deleteResource_NegativeProjectId_Fail() throws Exception {
        projectId = -1L;

        mockMvc.perform(delete(BASE_URL + DELETE_URL, projectId, resourceId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(content().string(containsString("Project id must be a positive integer")))
                .andExpect(jsonPath("$.['deleteResource.projectId']").value("Project id must be a positive integer"));
    }

    @Test
    @DisplayName("File delete fail: null projectId")
    void deleteResource_NullProjectId_Fail() throws Exception {
        projectId = null;

        mockMvc.perform(delete(BASE_URL + DELETE_URL, projectId, resourceId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("File delete fail: negative resourceId")
    void deleteResource_NegativeResourceId_Fail() throws Exception {
        resourceId = -1L;

        mockMvc.perform(delete(BASE_URL + DELETE_URL, projectId, resourceId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("File id must be a positive integer")))
                .andExpect(jsonPath("$.['deleteResource.resourceId']").value("File id must be a positive integer"));
    }

    @Test
    @DisplayName("File delete fail: user id not provided")
    void deleteResource_UserIdNotProvided_FailBadRequest() throws Exception {

        mockMvc.perform(delete(BASE_URL + DELETE_URL, projectId, resourceId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userId").value("Parameter cannot be null"))
                .andExpect(content().string(containsString("Parameter cannot be null")));
    }

    @Test
    @DisplayName("File delete fail: user id is negative")
    void deleteResource_NegativeUserId_FailBadRequest() throws Exception {
        userId = -1L;

        mockMvc.perform(delete(BASE_URL + DELETE_URL, projectId, resourceId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['deleteResource.userId']").value("User id must be a positive integer"))
                .andExpect(content().string(containsString("User id must be a positive integer")));
    }

    @Test
    @DisplayName("File update success")
    void updateResource_ValidFileAndParams_Success() throws Exception {
        resourceId = 6L;
        when(resourceService.updateResource(projectId, resourceId, userId, file)).thenReturn(resourceResponseDto);

        mockMvc.perform(multipart(BASE_URL + UPDATE_URL, projectId, resourceId)
                        .file(file)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(resourceId))
                .andExpect(jsonPath("$.message").value(String.format("File id: %d updated successfully", resourceId)));
    }

    @Test
    @DisplayName("File update fail: negative projectId")
    void updateResource_NegativeProjectId_Fail() throws Exception {
        projectId = -1L;

        mockMvc.perform(multipart(BASE_URL + UPDATE_URL, projectId, resourceId)
                        .file(file)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['updateResource.projectId']").value("Project id must be a positive integer"));
    }

    @Test
    @DisplayName("File update fail: negative resourceId")
    void updateResource_NegativeResourceId_Fail() throws Exception {
        resourceId = -1L;

        mockMvc.perform(multipart(BASE_URL + UPDATE_URL, projectId, resourceId)
                        .file(file)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['updateResource.resourceId']").value("File id must be a positive integer"));
    }

    @Test
    @DisplayName("File update fail: negative userId")
    void updateResource_NegativeUserId_Fail() throws Exception {
        userId = -1L;

        mockMvc.perform(multipart(BASE_URL + UPDATE_URL, projectId, resourceId)
                        .file(file)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['updateResource.userId']").value("User id must be a positive integer"));
    }

    @Test
    @DisplayName("File update fail: missed userId")
    void updateResource_MissedUserId_Fail() throws Exception {
        mockMvc.perform(multipart(BASE_URL + UPDATE_URL, projectId, resourceId)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userId").value("Parameter cannot be null"));
    }

    @Test
    @DisplayName("File update fail: file not provided")
    void updateResource_FiledNotProvided_Fail() throws Exception {
        mockMvc.perform(multipart(BASE_URL + UPDATE_URL, projectId, resourceId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.file").value("Value not provided"));
    }

    @Test
    @DisplayName("Download file success")
    void getResource_ValidFileAndParams_Success() throws Exception {
        byte[] content = "content".getBytes();
        when(resourceService.downloadResource(projectId, resourceId, userId)).thenReturn(content);

        mockMvc.perform(get(BASE_URL + GET_URL, projectId, resourceId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(content));
    }

    @Test
    @DisplayName("Download file fail: negative project id")
    void getResource_NegativeProjectId_Fail() throws Exception {
        projectId = -1L;

        mockMvc.perform(get(BASE_URL + GET_URL, projectId, resourceId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['getResource.projectId']").value("Project id must be a positive integer"));
    }

    @Test
    @DisplayName("Download file fail: negative resource id")
    void getResource_NegativeResourceId_Fail() throws Exception {
        resourceId = -1L;

        mockMvc.perform(get(BASE_URL + GET_URL, projectId, resourceId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['getResource.resourceId']").value("File id must be a positive integer"));
    }

    @Test
    @DisplayName("Download file fail: negative user id")
    void getResource_NegativeUserId_Fail() throws Exception {
        userId = -1L;

        mockMvc.perform(get(BASE_URL + GET_URL, projectId, resourceId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['getResource.userId']").value("User id must be a positive integer"));
    }

    @Test
    @DisplayName("Download file fail: missed user id")
    void getResource_MissedUserId_Fail() throws Exception {

        mockMvc.perform(get(BASE_URL + GET_URL, projectId, resourceId)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userId").value("Parameter cannot be null"));
    }

    private ResourceResponseDto mockResourceResponseDto() {
        return ResourceResponseDto.builder()
                .id(6L)
                .name("file")
                .key("project-folder/text.txt")
                .size(BigInteger.valueOf(1048576))
                .type(ResourceType.TEXT)
                .createdById(2L)
                .updatedById(3L)
                .createdAt(LocalDateTime.of(2023, 10, 1, 12, 0))
                .updatedAt(LocalDateTime.of(2023, 10, 2, 14, 30))
                .projectId(1L)
                .build();
    }

    private MockMultipartFile mockMultipartFile() {
        return new MockMultipartFile(
                "file",
                "text.txt",
                "text/plain",
                "content".getBytes()
        );
    }
}