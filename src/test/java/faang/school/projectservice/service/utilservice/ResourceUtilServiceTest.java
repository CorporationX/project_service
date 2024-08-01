package faang.school.projectservice.service.utilservice;

import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceUtilServiceTest {

    @InjectMocks
    private ResourceUtilService resourceUtilService;

    @Mock
    private ResourceRepository resourceRepository;

    @Test
    void testSave() {
        long resourceId = 1L;
        Resource resource = Resource.builder()
                .id(resourceId)
                .build();

        when(resourceRepository.save(resource)).thenReturn(resource);

        Resource result = resourceUtilService.save(resource);

        assertEquals(resource, result);
        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

    @Test
    void testGetByIdAndProjectId() {
        long resourceId = 1L;
        long projectId = 11L;
        Resource resource = Resource.builder()
                .id(resourceId)
                .build();

        when(resourceRepository.findByIdAndProjectId(resourceId, projectId)).thenReturn(Optional.of(resource));

        Resource result = resourceUtilService.getByIdAndProjectId(resourceId, projectId);
        assertEquals(resource, result);
        verify(resourceRepository, times(1)).findByIdAndProjectId(resourceId, projectId);
    }

    @Test
    void testGetByIdAndProjectId_notExists_throws() {
        long resourceId = 1L;
        long projectId = 11L;

        when(resourceRepository.findByIdAndProjectId(resourceId, projectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> resourceUtilService.getByIdAndProjectId(resourceId, projectId));
        verify(resourceRepository, times(1)).findByIdAndProjectId(resourceId, projectId);
    }

    @Test
    void testGetByIdAndProjectIdAndStatusNot() {
        long resourceId = 1L;
        long projectId = 11L;
        ResourceStatus statusNot = ResourceStatus.DELETED;
        Resource resource = Resource.builder()
                .id(resourceId)
                .status(ResourceStatus.ACTIVE)
                .build();

        when(resourceRepository.findResourceByIdAndProjectIdAndStatusNot(resourceId, projectId, statusNot))
                .thenReturn(Optional.of(resource));

        Resource result = resourceUtilService.getByIdAndProjectIdAndStatusNot(resourceId, projectId, statusNot);
        assertEquals(resource, result);
        verify(resourceRepository, times(1)).findResourceByIdAndProjectIdAndStatusNot(resourceId, projectId, statusNot);
    }

    @Test
    void testGetByIdAndProjectIdAndStatusNot_notExists_throws() {
        long resourceId = 1L;
        long projectId = 11L;
        ResourceStatus statusNot = ResourceStatus.DELETED;

        when(resourceRepository.findResourceByIdAndProjectIdAndStatusNot(resourceId, projectId, statusNot))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () ->
                resourceUtilService.getByIdAndProjectIdAndStatusNot(resourceId, projectId, statusNot));
        verify(resourceRepository, times(1)).findResourceByIdAndProjectIdAndStatusNot(resourceId, projectId, statusNot);
    }

    @Test
    void testGetAllByProjectId() {
        long projectId = 11L;
        List<Resource> resources = List.of(
                Resource.builder()
                        .id(1L)
                        .project(Project.builder().id(projectId).build())
                        .build(),
                Resource.builder()
                        .id(2L)
                        .project(Project.builder().id(projectId).build())
                        .build()
        );

        when(resourceRepository.findAllByProjectId(projectId)).thenReturn(resources);

        List<Resource> result = resourceUtilService.getAllByProjectId(projectId);
        assertEquals(resources, result);
        verify(resourceRepository, times(1)).findAllByProjectId(projectId);
    }

}