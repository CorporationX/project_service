package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectUpdateDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTests {
    @Mock
    private ResourceService resourceService;
    @Mock
    private ProjectService projectService;
    @Mock
    private ProjectValidator projectValidator;
    @InjectMocks
    private ProjectController projectController;
    @Mock
    private UserContext userContext;

    @Test
    void testCreateProject_ShouldCallServiceMethod() {
        var projectDto = ProjectDto.builder().name("test").description("test").build();
        projectController.createProject(projectDto);
        verify(projectService, times(1)).createProject(projectDto);
    }

    @Test
    void testUpdateProject_ShouldCallServiceMethod() {
        var projectUpDateDto = ProjectUpdateDto.builder().status(ProjectStatus.COMPLETED).build();
        projectController.updateProject(1L, projectUpDateDto);
        verify(projectService, times(1))
                .updateProject(1L, projectUpDateDto);
    }

    @Test
    void testGetAllProjectsWithFilter_ShouldCallServiceMethod() {
        var projectFilterDto = ProjectFilterDto.builder().name("Java").build();
        projectController.getAllProjectsWithFilter(projectFilterDto);
        verify(projectService, times(1)).getAllProjectsWithFilter(projectFilterDto);
    }

    @Test
    void testGetAllProjects_ShouldCallServiceMethod() {
        projectController.getAllProjects();
        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void testGetProjectById_ShouldCallServiceMethod() {
        projectController.getProjectById(1L);
        verify(projectService, times(1)).getProjectById(1L);
    }

    @Test
    void shouldAddCoverToProject() {
        MultipartFile cover = null;
        long projectId = 1L;
        projectController.addCover(projectId, cover);
        Mockito.verify(resourceService).addCoverToProject(projectId, userContext.getUserId(), cover);
    }

    @Test
    void shouldGetCover() throws IOException {
        byte[] result = new byte[]{1, 2, 3};
        long resourceId = 1L;
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(resourceService.downloadCoverByProjectId(resourceId)).thenReturn(inputStream);
        Mockito.when(inputStream.readAllBytes()).thenReturn(result);
        projectController.getCover(resourceId);
        Mockito.verify(resourceService).downloadCoverByProjectId(resourceId);
    }
}
