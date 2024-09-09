package faang.school.projectservice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.Optional;

import faang.school.projectservice.dto.client.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private Project project;
    private TeamMember teamMember;
    private Resource resource;
    private MultipartFile file;

    @BeforeEach
    public void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setStorageSize(BigInteger.valueOf(500L));
        project.setMaxStorageSize(BigInteger.valueOf(1000L));

        teamMember = new TeamMember();
        teamMember.setId(1L);

        resource = new Resource();
        resource.setKey("test/key");
        resource.setSize(BigInteger.valueOf(100L));
        resource.setCreatedBy(TeamMember.builder().id(1L).build());
        resource.setProject(Project.builder().id(1L).ownerId(1L).storageSize(BigInteger.TEN).build());

        file = new MockMultipartFile("testFile", "test content".getBytes());
    }

    @Test
    public void testAddResource() {
        when(projectRepository.getProjectById(any())).thenReturn(project);
        when(s3Service.upload(any(), any())).thenReturn(resource);
        when(teamMemberRepository.findById(any())).thenReturn(teamMember);
        when(resourceRepository.save(any())).thenReturn(resource);
        when(resourceMapper.toDTO(any())).thenReturn(new ResourceDto());

        ResourceDto result = resourceService.addResource(file, 1L, 1L);

        verify(projectRepository, times(1)).getProjectById(1L);
        verify(s3Service, times(1)).upload(file, "Test Project1");
        verify(teamMemberRepository, times(1)).findById(1L);
        verify(resourceRepository, times(1)).save(resource);
        verify(resourceMapper, times(1)).toDTO(resource);
    }

    @Test
    public void testDeleteResource() {
        when(resourceRepository.findById(any())).thenReturn(Optional.of(resource));
        when(teamMemberRepository.findById(any())).thenReturn(teamMember);

        resourceService.deleteResource(1L, 1L);

        verify(resourceRepository, times(1)).findById(1L);
        verify(teamMemberRepository, times(1)).findById(1L);
        verify(s3Service, times(1)).delete("test/key");
        verify(resourceRepository, times(1)).save(resource);
    }

    @Test
    public void testDeleteResource_ResourceNotFound() {
        when(resourceRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            resourceService.deleteResource(1L, 1L);
        });

        verify(resourceRepository, times(1)).findById(1L);
        verify(teamMemberRepository, never()).findById(any());
        verify(s3Service, never()).delete(any());
        verify(resourceRepository, never()).save(any());
    }
}