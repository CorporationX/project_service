package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResourceFileDto;
import faang.school.projectservice.exception.GlobalExceptionHandler;
import faang.school.projectservice.exception.ResourceHandlingException;
import faang.school.projectservice.service.projectresource.ProjectResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.nio.file.AccessDeniedException;
import java.util.List;

import static faang.school.projectservice.model.ResourceStatus.ACTIVE;
import static faang.school.projectservice.model.ResourceType.TEXT;
import static faang.school.projectservice.model.TeamRole.DEVELOPER;
import static faang.school.projectservice.model.TeamRole.MANAGER;
import static faang.school.projectservice.model.TeamRole.OWNER;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProjectResourceControllerTest {

    @Mock
    private ProjectResourceService projectResourceService;

    @InjectMocks
    private ProjectResourceController projectResourceController;

    private MockMvc mockMvc;
    private ResourceFileDto resourceFileDto;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectResourceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        resourceFileDto = ResourceFileDto.builder()
                .id(1L)
                .name("test.txt")
                .size(BigInteger.valueOf(100))
                .allowedRoles(List.of(OWNER, MANAGER, DEVELOPER))
                .type(TEXT)
                .status(ACTIVE)
                .projectId(1L)
                .build();

        file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Test content".getBytes()
        );
    }

    @Test
    void testUploadFileValidRequest() throws Exception {
        when(projectResourceService.uploadFile(1L, file)).thenReturn(resourceFileDto);

        mockMvc.perform(multipart("/api/v1/resources/upload/{projectId}", 1L)
                        .file((MockMultipartFile) file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test.txt"))
                .andExpect(jsonPath("$.projectId").value(1L));

        verify(projectResourceService, times(1)).uploadFile(1L, file);
        verifyNoMoreInteractions(projectResourceService);
    }

    @Test
    void testUploadFileInvalidProjectId() throws Exception {
        mockMvc.perform(multipart("/api/v1/resources/upload/{projectId}", "invalid")
                        .file((MockMultipartFile) file))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(projectResourceService);
    }

    @Test
    void testDownloadFileValidResourceId() throws Exception {
        when(projectResourceService.downloadFile(anyLong())).thenReturn(file.getInputStream());
        when(projectResourceService.getResourceInfo(anyLong())).thenReturn(resourceFileDto);

        mockMvc.perform(get("/api/v1/resources/download/{resourceId}", 1L))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"test.txt\""))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(content().bytes(file.getBytes()));

        verify(projectResourceService).downloadFile(1L);
        verify(projectResourceService).getResourceInfo(1L);
    }

    @Test
    void testDownloadFileInvalidResourceId() throws Exception {
        when(projectResourceService.downloadFile(anyLong()))
                .thenThrow(new ResourceHandlingException("Resource Handling Error"));

        mockMvc.perform(get("/api/v1/resources/download/{resourceId}", 999L))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Resource Handling Error"));

        verifyNoMoreInteractions(projectResourceService);
    }

    @Test
    void testDeleteFileValidResourceId() throws Exception {
        mockMvc.perform(delete("/api/v1/resources/delete/{resourceId}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteFileAccessDenied() throws Exception {
        Long resourceId = 999L;

        doThrow(new AccessDeniedException("Access Denied"))
                .when(projectResourceService)
                .deleteFile(resourceId);

        mockMvc.perform(delete("/api/v1/resources/delete/{resourceId}", resourceId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access Denied"));

        verifyNoMoreInteractions(projectResourceService);
    }
}
