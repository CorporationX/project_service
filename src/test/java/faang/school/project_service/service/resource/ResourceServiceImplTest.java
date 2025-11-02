package faang.school.project_service.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.resource.ResourceServiceImpl;
import faang.school.projectservice.service.s3.S3ServiceImpl;
import faang.school.projectservice.validation.resource.ResourceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.unit.DataSize;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceImplTest {

    private final int fileSizeMax = 5;
    private final ResourceMapper resourceMapper = Mappers.getMapper(ResourceMapper.class);
    private final byte[] content = new byte[(int) DataSize.ofMegabytes(6).toBytes()];
    private final MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", content);
    private final Project project = Project.builder()
            .id(1L)
            .name("Test project")
            .storageSize(new BigInteger("1"))
            .maxStorageSize(new BigInteger("2000000"))
            .build();
    private final Resource resourceToUpload = Resource.builder()
            .id(5663L)
            .name("Resource to upload")
            .build();
    private final Resource resource = Resource.builder()
            .id(234L)
            .build();
    private final BigInteger projectNewStorageSize
            = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));

    @Captor
    private ArgumentCaptor<Resource> resourceArgumentCaptor;
    @Captor
    private ArgumentCaptor<Project> projectArgumentCaptor;

    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private S3ServiceImpl s3Service;
    @Mock
    private ResourceValidator resourceValidator;

    private ResourceServiceImpl resourceService;

    @BeforeEach
    void setup() {
        resourceService = new ResourceServiceImpl(resourceRepository, projectRepository, s3Service, resourceMapper,
                resourceValidator);
        ReflectionTestUtils.setField(resourceService, "projectAvatarFileSizeMax", fileSizeMax);
    }

    @Test
    void testAddProjectAvatarThrowsExceptionIfProjectNotFound() {
        when(projectRepository.getByIdOrThrow(project.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> resourceService.addProjectAvatar(project.getId(), file));
    }

    @Test
    void testAddProjectAvatarPositive() {
        when(projectRepository.getByIdOrThrow(project.getId())).thenReturn(project);
        when(resourceValidator.validateImageDimensions(file)).thenReturn(file);
        when(s3Service.uploadFile(Mockito.eq(file), anyString())).thenReturn(resourceToUpload);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        ResourceDto savedResourceDto = resourceService.addProjectAvatar(project.getId(), file);

        verify(resourceValidator).validateFileSize(file, fileSizeMax);
        verify(resourceValidator).validateProjectStorageSize(project, file);
        verify(resourceValidator).validateImageDimensions(file);
        verify(s3Service).uploadFile(file, project.getId() + project.getName());
        verify(resourceRepository).save(resourceArgumentCaptor.capture());
        verify(projectRepository).save(projectArgumentCaptor.capture());

        Resource capturedResourceToSave = resourceArgumentCaptor.getValue();
        Project capturedProjectToSave = projectArgumentCaptor.getValue();

        assertEquals(resourceToUpload.getId(), capturedResourceToSave.getId());
        assertEquals(resource.getId(), savedResourceDto.id());
        assertEquals(resource.getId().toString(), capturedProjectToSave.getCoverImageId());
        assertEquals(projectNewStorageSize, project.getStorageSize());
    }
}