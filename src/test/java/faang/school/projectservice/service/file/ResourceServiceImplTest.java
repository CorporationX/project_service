package faang.school.projectservice.service.file;

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

    @Test
    void addResource() throws IOException, ImageReadException {
        Long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);
        project.setName("testProject");
        project.setStorageSize(BigInteger.ZERO);
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);

        Resource resource = new Resource();
        resource.setId(123L);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        Resource uploadedResource = new Resource();
        uploadedResource.setName("test.jpg");
        uploadedResource.setKey("test.jpg");
        uploadedResource.setSize(BigInteger.valueOf(1024));
        uploadedResource.setType(ResourceType.IMAGE);
        when(s3Service.uploadFile(any(MultipartFile.class), anyString())).thenReturn(uploadedResource);

        ResourceDto resourceDto = new ResourceDto();
        when(resourceMapper.toDto(any(Resource.class))).thenReturn(resourceDto);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(image, "jpg", baos);
        byte[] fileContent = baos.toByteArray();
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", fileContent); //  Имитация файла

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
    void updateResource() throws IOException, ImageReadException  {
        Long userDtoId = 1L;
        Long projectId = 1L;
        Long resourceId = 123L;

        Project project = new Project();
        project.setId(projectId);
        project.setName("testProject");
        project.setStorageSize(BigInteger.ZERO);

        Resource resourceFromBD = new Resource();
        resourceFromBD.setId(resourceId);
        resourceFromBD.setKey("old_key.jpg");
        resourceFromBD.setSize(BigInteger.valueOf(512));
        resourceFromBD.setProject(project);

        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(userDtoId);
        resourceFromBD.setCreatedBy(teamMember);

        Resource uploadedResource = new Resource();
        uploadedResource.setName("test.jpg");
        uploadedResource.setKey("new_key.jpg");
        uploadedResource.setSize(BigInteger.valueOf(1024));
        uploadedResource.setType(ResourceType.IMAGE);

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resourceFromBD));
        when(s3Service.uploadFile(any(MultipartFile.class), anyString())).thenReturn(uploadedResource);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resourceFromBD);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ResourceDto resourceDto = new ResourceDto();
        when(resourceMapper.toDto(any(Resource.class))).thenReturn(resourceDto);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(image, "jpg", baos);
        byte[] fileContent = baos.toByteArray();
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", fileContent);


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
    void deleteResource() {
        Resource resource = new Resource();
        resourceRepository.delete(resource);
        verify(resourceRepository, times(1)).delete(resource);
    }
}