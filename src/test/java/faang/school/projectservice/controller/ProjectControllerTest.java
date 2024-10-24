package faang.school.projectservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.projectservice.model.dto.ProjectDto;
import faang.school.projectservice.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @InjectMocks
    private ProjectController projectController;

    @Mock
    private ProjectServiceImpl projectService;

    @Mock
    private MultipartFile file;

    private ProjectDto projectDto = new ProjectDto();

    @Test
    void testUploadCoverImage() throws Exception {
        long projectId = 1L;
        projectDto.setId(1L);
        projectDto.setCoverImageId("coverImageId");
        when(projectService.uploadCoverImage(projectId, file)).thenReturn(projectDto);
        ProjectDto result = projectController.uploadCoverImage(projectId, file);
        assertNotNull(result);
        assertEquals(projectDto, result);
        verify(projectService, times(1)).uploadCoverImage(projectId, file);
    }

    @Test
    void testDownloadCoverImage() {
        long projectId = 1L;
        InputStream mockInputStream = mock(InputStream.class);
        when(projectService.downloadCoverImage(projectId)).thenReturn(mockInputStream);

        InputStream result = projectController.downloadCoverImageBy(projectId);

        assertNotNull(result);
        assertEquals(mockInputStream, result);
        verify(projectService, times(1)).downloadCoverImage(projectId);
    }

    @Test
    void testDeleteCoverImage() {
        long projectId = 1L;
        when(projectService.deleteCoverImage(projectId)).thenReturn(projectDto);

        ProjectDto result = projectController.deleteCoverImage(projectId);

        assertNotNull(result);
        assertEquals(projectDto, result);
        verify(projectService, times(1)).deleteCoverImage(projectId);
    }
}