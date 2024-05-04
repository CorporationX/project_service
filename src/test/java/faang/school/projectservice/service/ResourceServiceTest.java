package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.SizeExceeded;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.service.S3.S3ServiceImpl;
import faang.school.projectservice.validator.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest extends SetUpFileForResource {
    @Mock
    S3ServiceImpl s3Service;
    @Mock
    ResourceRepository resourceRepository;
    @Mock
    ProjectService projectService;
    @Mock
    ResourceMapper resourceMapper;
    @Mock
    UserServiceClient userServiceClient;
    @Mock
    ResourceValidator resourceValidator;
    @InjectMocks
    ResourceService resourceService;

    @Test
    public void testUploadResource_StorageSizeExceeded() {
        when(projectService.getProjectById(secondProject.getId())).thenReturn(secondProject);
        when(resourceValidator.validateForTeamMemberExistence(userId, projectId)).thenReturn(firstTeamMember);
        assertThrows(SizeExceeded.class, () -> resourceService.uploadResource(projectId, file, userId));
    }

    @Test
    public void testUploadResource() {
        when(projectService.getProjectById(firstProject.getId())).thenReturn(firstProject);
        when(resourceValidator.validateForTeamMemberExistence(userId, projectId)).thenReturn(firstTeamMember);
        when(s3Service.uploadFile(file, folder)).thenReturn(firstResource);
        when(resourceRepository.save(firstResource)).thenReturn(firstResource);
        when(projectService.save(firstProject)).thenReturn(firstProject);

        resourceService.uploadResource(firstProject.getId(), file, userId);

        verify(resourceMapper, times(1)).toDto(firstResource);
    }

    @Test
    public void testDownLoadResource_ResourceNotFound() {
        when(resourceRepository.findById(firstResource.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> resourceService.downloadResource(firstResource.getId()));
    }

    @Test
    public void testDownLoadResource() {
        when(resourceRepository.findById(firstResource.getId())).thenReturn(Optional.of(firstResource));
        when(s3Service.downloadFile(firstResource.getKey())).thenReturn(any(InputStream.class));
        resourceService.downloadResource(firstResource.getId());
    }

    @Test
    public void testDeleteResource() {
        when(userServiceClient.getUser(userId)).thenReturn(Mockito.any());
        when(resourceRepository.getReferenceById(firstResource.getId())).thenReturn(firstResource);

        resourceService.deleteResource(firstResource.getId(), userId);

        verify(s3Service, times(1)).deleteFile(firstResource.getKey());
        verify(resourceRepository, times(1)).delete(firstResource);
        verify(resourceValidator, times(1)).validateForOwner(userId, firstResource);
    }
}