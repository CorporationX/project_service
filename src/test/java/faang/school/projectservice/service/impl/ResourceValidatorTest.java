package faang.school.projectservice.service.impl;

import faang.school.projectservice.config.filestorage.GalleryProperties;
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
        Long userInProjectId = 1L;
        Long userNotInProjectId = 2L;
        Long publicProjectId = 222L;
        Long privateProjectId = 223L;
        Mockito.when(projectValidatorMock.isProjectPublic(publicProjectId)).thenReturn(true);
        Mockito.when(projectValidatorMock.isUserParticipatedInProject(userInProjectId, publicProjectId)).thenReturn(true);
        resourceValidator.validateUserCanDownloadFromProject(userInProjectId, publicProjectId);

        Mockito.when(projectValidatorMock.isProjectPublic(privateProjectId)).thenReturn(false);
        Mockito.when(projectValidatorMock.isUserParticipatedInProject(userInProjectId, privateProjectId)).thenReturn(true);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> resourceValidator.validateUserCanDownloadFromProject(userInProjectId, privateProjectId));

        Mockito.when(projectValidatorMock.isProjectPublic(publicProjectId)).thenReturn(true);
        Mockito.when(projectValidatorMock.isUserParticipatedInProject(userNotInProjectId, publicProjectId)).thenReturn(false);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> resourceValidator.validateUserCanDownloadFromProject(userNotInProjectId, publicProjectId));

        Mockito.when(projectValidatorMock.isProjectPublic(privateProjectId)).thenReturn(false);
        Mockito.when(projectValidatorMock.isUserParticipatedInProject(userNotInProjectId, privateProjectId)).thenReturn(false);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> resourceValidator.validateUserCanDownloadFromProject(userNotInProjectId, privateProjectId));
    }

    @Test
    @DisplayName("Test overload resource to project")
    void validateResourcesOversize() {
        Long projectId = 222L;
        Mockito.when(projectServiceMock.getProjectResourceIds(projectId)).thenReturn(resourceIds);
        Mockito.when(galleryPropertiesMock.getMaxFiles()).thenReturn(50);

        resourceValidator.validateResourcesOversize(projectId);
        resourceIds.add(1050L);
        resourceValidator.validateResourcesOversize(projectId);
        resourceIds.add(10005L);
        Assert.assertThrows(RuntimeException.class,
                () -> resourceValidator.validateResourcesOversize(projectId));
    }
}