package faang.school.projectservice.service.impl;

import faang.school.projectservice.config.filestorage.GalleryProperties;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@PropertySource("classpath:application.yaml")
class ResourceValidatorTest {
    @Mock
    private ProjectServiceImpl projectServiceMock;
    @Mock
    private ProjectValidator projectValidatorMock;
    @Mock
    private GalleryProperties galleryPropertiesMock;
    @InjectMocks
    private ResourceValidator resourceValidator;
    private final List<Long> resourceIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 49; i++) {
            resourceIds.add((long) i);
        }
    }

    @Test
    @DisplayName("Test user can download resource")
    void validateUserCanDownloadResource() {

        Long publicProjectId = 222L;
        Project publicProject = Project.builder().id(publicProjectId).build();
        Mockito.when(projectValidatorMock.isProjectPublic(publicProject)).thenReturn(true);

        Long userInProjectId = 1L;
        Mockito.when(projectValidatorMock.isUserParticipatedInProject(userInProjectId, publicProject))
                .thenReturn(true);
        resourceValidator.validateUserCanDownloadFromProject(userInProjectId, publicProject);

        Long privateProjectId = 223L;
        Project privateProject = Project.builder().id(privateProjectId).build();
        Mockito.when(projectValidatorMock.isProjectPublic(privateProject)).thenReturn(false);
        Mockito.when(projectValidatorMock.isUserParticipatedInProject(userInProjectId, privateProject))
                .thenReturn(true);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> resourceValidator.validateUserCanDownloadFromProject(userInProjectId, privateProject));

        Long userNotInProjectId = 2L;
        Mockito.when(projectValidatorMock.isProjectPublic(publicProject)).thenReturn(true);
        Mockito.when(projectValidatorMock.isUserParticipatedInProject(userNotInProjectId, publicProject))
                .thenReturn(false);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> resourceValidator.validateUserCanDownloadFromProject(userNotInProjectId, publicProject));

        Mockito.when(projectValidatorMock.isProjectPublic(privateProject)).thenReturn(false);
        Mockito.when(projectValidatorMock.isUserParticipatedInProject(userNotInProjectId, privateProject))
                .thenReturn(false);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> resourceValidator.validateUserCanDownloadFromProject(userNotInProjectId, privateProject));
    }

    @Test
    @DisplayName("Test overload resource to project")
    void validateResourcesOversize() {
        Long projectId = 222L;
        Project project = Project.builder().id(projectId).build();
        Mockito.when(projectServiceMock.getProjectResourceIds(projectId)).thenReturn(resourceIds);
        Mockito.when(galleryPropertiesMock.getMaxFiles()).thenReturn(50);

        resourceValidator.validateResourcesOversize(project);
        resourceIds.add(1050L);
        resourceValidator.validateResourcesOversize(project);
        resourceIds.add(10005L);
        Assert.assertThrows(RuntimeException.class,
                () -> resourceValidator.validateResourcesOversize(project));
    }
}