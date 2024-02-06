package faang.school.projectservice.service.resource;

import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.mapper.ResourceMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStreamImpl;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @Mock
    private S3Service s3Service;
    @Mock
    private ProjectService projectService;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Spy
    private final ResourceMapperImpl resourceMapper = new ResourceMapperImpl();
    @InjectMocks
    private ResourceService resourceService;
    @Captor
    private ArgumentCaptor<Resource> resourceCaptor;



    @Test
    void shouldAddCoverToProject() throws IOException {
        //Arrange
        Project project = new Project();
        long projectId = 1L;
        project.setName("Project");
        project.setStorageSize(new BigInteger("100"));
        TeamMember teamMember = new TeamMember();
        long userId = 1L;
        Resource resource = new Resource();
        resource.setKey("key");
        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        String stream = "stream";
        InputStream inputStream = new ByteArrayInputStream(stream.getBytes());
        MultipartFile file = new MockMultipartFile("file", "file.png", "image/png", stream.getBytes());


        Mockito.when(projectService.getProjectById(projectId))
                .thenReturn(project);
        Mockito.when(teamMemberRepository.findById(userId))
                .thenReturn(teamMember);
        Mockito.when(file.getSize()).thenReturn(100L);
        Mockito.when(s3Service.uploadFile(file, project.getId() + project.getName()))
                .thenReturn(resource);
        Mockito.when(file.getInputStream()).thenReturn(inputStream);
        Mockito.when(ImageIO.read(inputStream)).thenReturn(bufferedImage);

        resourceService.addCoverToProject(projectId, userId, file);

        Mockito.verify(resourceRepository).save(resource);
        Mockito.verify(projectService).save(project);
        Mockito.verify(resourceMapper).toDto(resource);
    }

    @Test
    void downloadCover() {
    }
}