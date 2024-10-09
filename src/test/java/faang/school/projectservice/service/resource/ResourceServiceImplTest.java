package faang.school.projectservice.service.resource;


import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.mapper.resource.ResourceMapperImpl;
import faang.school.projectservice.model.entity.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.S3Service;
import faang.school.projectservice.service.impl.resource.ResourceServiceImpl;
import faang.school.projectservice.validator.resource.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceValidator validator;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private S3Service s3Service;

    @Spy
    private ResourceMapperImpl resourceMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Captor
    private ArgumentCaptor<Resource> resourceCaptor;

    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private Resource resource;
    private MultipartFile file;
    private TeamMember member;
    private final long userId = 1L;
    private final long projectId = 1L;
    private final long resourceId = 1L;
    private final String key = "fileKey";

    @BeforeEach
    void setup() {
        file = new MockMultipartFile("someFile", "original", "text", new byte[10000]);

        Project project = Project.builder()
                .id(projectId)
                .name("Test Project")
                .storageSize(BigInteger.ZERO)
                .maxStorageSize(BigInteger.valueOf(2000000))
                .build();
        member = TeamMember.builder()
                .id(3L)
                .userId(userId)
                .team(Team.builder().project(project).build())
                .roles(List.of())
                .build();

        resource = Resource.builder()
                .name(file.getName())
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .createdBy(member)
                .updatedBy(member)
                .updatedAt(LocalDateTime.now())
                .project(member.getTeam().getProject())
                .allowedRoles(member.getRoles())
                .build();
    }

    @Test
    void testSaveResourceOk() {
        when(teamMemberRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(member);
        when(s3Service.uploadFile(any(MultipartFile.class), anyString())).thenReturn(key);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        resourceService.saveResource(projectId, file, userId);

        verify(projectRepository).save(projectCaptor.capture());
        verify(resourceRepository).save(resourceCaptor.capture());
        verify(s3Service).uploadFile(any(MultipartFile.class), anyString());

        assertEquals(resourceCaptor.getValue().getType(), ResourceType.TEXT);
        assertEquals(resourceCaptor.getValue().getProject().getId(), member.getTeam().getProject().getId());
        assertEquals(projectCaptor.getValue().getStorageSize(), BigInteger.valueOf(file.getSize()));
    }

    @Test
    void testGetFileOk() throws IOException {
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(resource));
        when(s3Service.getFile(key)).thenReturn(new InputStreamResource(file.getInputStream()));

        resourceService.getFile(projectId, userId, resourceId);

        verify(resourceRepository).findById(anyLong());
        verify(s3Service).getFile(anyString());
    }

    @Test
    void testGetFileResourceNotFound() {
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> resourceService.getFile(projectId, userId, resourceId));
    }

    @Test
    void testDeleteResource(){
        when(teamMemberRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(member);
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));

        resourceService.deleteResource(projectId, userId, resourceId);

        verify(resourceRepository).save(resourceCaptor.capture());
        verify(projectRepository).save(projectCaptor.capture());
        verify(s3Service).deleteFile(anyString());

        assertEquals(resourceCaptor.getValue().getStatus(), ResourceStatus.DELETED);
        assertEquals(resourceCaptor.getValue().getSize(), BigInteger.ZERO);
        assertNull(resourceCaptor.getValue().getKey());
        assertEquals(projectCaptor.getValue().getStorageSize(), BigInteger.valueOf(- file.getSize()));
    }

}
