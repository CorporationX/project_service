package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import org.apache.commons.imaging.ImageReadException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @InjectMocks
    ResourceServiceImpl resourceService;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    ResourceRepository resourceRepository;

    @Mock
    S3Service s3Service;

    @Mock
    ResourceMapper resourceMapper;

    MultipartFile file;
    Project project = new Project();
    Resource resourceFromBD = new Resource();
    TeamMember teamMember = new TeamMember();
    Resource uploadedResource = new Resource();
    Resource resource = new Resource();
    ResourceDto resourceDto = new ResourceDto();
    Long userDtoId = 1L;
    Long projectId = 1L;
    Long resourceId = 123L;

    @BeforeEach
    void setUp() throws IOException {
        project.setId(projectId);
        project.setName("testProject");
        project.setStorageSize(BigInteger.ZERO);

        resourceFromBD.setId(resourceId);
        resourceFromBD.setKey("old_key.jpg");
        resourceFromBD.setSize(BigInteger.valueOf(512));
        resourceFromBD.setProject(project);

        teamMember.setUserId(userDtoId);
        resourceFromBD.setCreatedBy(teamMember);

        resource.setId(123L);
        resource.setCreatedBy(teamMember);
        resource.setProject(project);

        uploadedResource.setName("test.jpg");
        uploadedResource.setKey("new_key.jpg");
        uploadedResource.setSize(BigInteger.valueOf(1024));
        uploadedResource.setType(ResourceType.IMAGE);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(image, "jpg", baos);
        byte[] fileContent = baos.toByteArray();
        file = new MockMultipartFile("file", "test.jpg", "image/jpeg", fileContent);
    }

    @Test
    void testAddResource_Success() throws IOException, ImageReadException {
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);
        when(s3Service.uploadFile(any(MultipartFile.class), eq(projectId + "testProject"))).thenReturn(uploadedResource);
        when(resourceMapper.toDto(any(Resource.class))).thenReturn(resourceDto);
        ResourceDto result = resourceService.addResource(projectId, file);

        verify(projectRepository, times(1)).getProjectById(anyLong());
        verify(resourceRepository, times(1)).save(any(Resource.class));
        verify(s3Service, times(1)).uploadFile(file, projectId + "testProject");
        verify(projectRepository, times(1)).save(project);
        assertEquals(BigInteger.valueOf(823), project.getStorageSize());
        assertEquals(String.valueOf(resource.getId()), project.getCoverImageId());
        assertEquals(resourceDto, result);
    }

    @Test
    void testUpdateResource_Success() throws IOException, ImageReadException {
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resourceFromBD));
        when(s3Service.uploadFile(any(MultipartFile.class), anyString())).thenReturn(uploadedResource);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resourceFromBD);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ResourceDto resourceDto = new ResourceDto();
        when(resourceMapper.toDto(any(Resource.class))).thenReturn(resourceDto);

        ResourceDto result = resourceService.updateResource(resourceId, userDtoId, file);

        verify(resourceRepository, times(1)).findById(resourceId);
        verify(s3Service, times(1)).uploadFile(file, projectId + "testProject");
        verify(resourceRepository, times(1)).save(resourceFromBD);
        verify(projectRepository, times(1)).save(project);
        assertEquals(uploadedResource.getKey(), resourceFromBD.getKey());
        assertEquals(uploadedResource.getSize(), resourceFromBD.getSize());
        assertEquals(uploadedResource.getName(), resourceFromBD.getName());
        assertEquals(uploadedResource.getType(), resourceFromBD.getType());
        assertEquals(BigInteger.valueOf(823), project.getStorageSize());
        assertEquals(resourceDto, result);
    }

    @Test
    void testDeleteResource_Success() {
        Resource resource = new Resource();
        resourceRepository.delete(resource);
        verify(resourceRepository, times(1)).delete(resource);
    }

    @Test
    void testNotPermissions_Exception() {
        Long userId = 2L;
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[1024]);
            resourceService.updateResource(resource.getId(), userId, file);
        });
    }

    @Test
    void testFileIsTooBig_Exception() {
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[6000000]);
            resourceService.updateResource(resource.getId(), userDtoId, file);
        });
    }
}