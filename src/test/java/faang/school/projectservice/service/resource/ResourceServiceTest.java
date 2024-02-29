package faang.school.projectservice.service.resource;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.s3.CoverHandler;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.Optional;

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
    @Mock
    private CoverHandler coverHandler;
    @Mock
    private ProjectValidator projectValidator;
    @InjectMocks
    private ResourceService resourceService;


    @Test
    void shouldAddCoverToProject() {
        //Arrange
        long userId = 1L;
        long projectId = 1L;
        Project project = Project.builder()
                .id(1L)
                .name("Project")
                .storageSize(new BigInteger("0"))
                .maxStorageSize(new BigInteger("1000"))
                .build();
        TeamMember teamMember = new TeamMember();
        Resource resource = new Resource();
        resource.setKey("key");
        MultipartFile file = new MockMultipartFile("file", "file.png", "image/png", "file".getBytes());

        Mockito.when(projectService.getById(projectId))
                .thenReturn(project);
        Mockito.when(teamMemberRepository.findById(userId))
                .thenReturn(teamMember);
        Mockito.when(resourceRepository.save(resource)).thenReturn(resource);
        Mockito.when(s3Service.uploadFile(file, project.getId() + project.getName()))
                .thenReturn(resource);

        resourceService.addCoverToProject(projectId, userId, file);

        Mockito.verify(resourceRepository).save(resource);
        Mockito.verify(projectService).save(project);
        Mockito.verify(resourceMapper).toDto(resource);
        assertEquals(project.getCoverImageId(), resource.getKey());
    }



    @Test
    void downloadCoverFailTest() {
        long projectId = 1L;
        assertThrows(EntityNotFoundException.class,
                () -> resourceService.downloadCoverByProjectId(projectId));
    }

    @Test
    void downloadCoverSuccessTest() {
        long projectId = 1L;
        Resource resource = new Resource();
        resource.setKey("key");
        Mockito.when(resourceRepository.findResourceByProjectId(projectId))
                .thenReturn(Optional.of(resource));
        resourceService.downloadCoverByProjectId(projectId);
        Mockito.verify(s3Service).downloadFile(resource.getKey());
    }
}