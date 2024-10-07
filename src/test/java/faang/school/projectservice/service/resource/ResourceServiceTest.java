package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.StorageSizeExceededException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    @InjectMocks
    ResourceServiceImpl resourceService;

    @Mock
    private ProjectService projectService;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private UserContext userContext;

    @Spy
    private ResourceMapper mapper = Mappers.getMapper(ResourceMapper.class);

    private TeamMember teamMember;
    private Resource resource;
    private Project project;
    private MultipartFile multipartFile;
    private Long userId;
    private Long projectId;
    private Long resourceId;
    private Long invalidUserId;
    private String projectName;
    private String resourceKey;
    private BigInteger projectStorageSize = BigInteger.valueOf(2048);
    private BigInteger projectMaxStorageSize = BigInteger.valueOf(8192);
    private BigInteger resourceSize = BigInteger.valueOf(2048);

    @BeforeEach
    void setUp() {
        teamMember = new TeamMember();
        project = new Project();
        resource = new Resource();
        userId = 1L;
        projectId = 1L;
        projectName = "projectName";
        resourceId = 1L;
        resourceKey = "resourceKey";
        invalidUserId = 2L;
    }

    @Test
    void addResource_validResource_returnsResourceDto() {
        byte[] fileContent = new byte[2048];
        multipartFile = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                fileContent
        );
        project.setStorageSize(projectStorageSize);
        project.setMaxStorageSize(projectMaxStorageSize);
        project.setId(projectId);
        project.setName(projectName);

        when(userContext.getUserId()).thenReturn(userId);
        when(teamMemberRepository.findById(userId)).thenReturn(teamMember);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(s3Service.uploadFile(any(MultipartFile.class), any(String.class))).thenReturn(resource);
        when(resourceRepository.save(resource)).thenReturn(resource);

        resourceService.addResource(projectId, multipartFile);

        verify(resourceRepository).save(resource);
        verify(projectService).save(any(Project.class));
        verify(mapper).toDto(resource);
    }

    @Test
    void addResource_storageSizeExceeded_throwsStorageSizeExceededException() {
        byte[] fileContent = new byte[8192];
        multipartFile = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                fileContent
        );
        project.setStorageSize(projectStorageSize);
        project.setMaxStorageSize(projectMaxStorageSize);
        project.setId(projectId);
        project.setName(projectName);

        when(userContext.getUserId()).thenReturn(userId);
        when(teamMemberRepository.findById(userId)).thenReturn(teamMember);
        when(projectService.getProjectById(projectId)).thenReturn(project);

        assertThrows(StorageSizeExceededException.class, () -> resourceService.addResource(projectId, multipartFile));

        verify(resourceRepository, never()).save(resource);
        verify(projectService, never()).save(any(Project.class));
        verify(mapper, never()).toDto(resource);
    }

    @Test
    void deleteResource_validRequest_s3ServiceCalled() {
        teamMember.setId(userId);
        resource.setCreatedBy(teamMember);
        resource.setKey(resourceKey);
        resource.setProject(project);
        resource.setSize(resourceSize);
        project.setStorageSize(projectStorageSize);

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.ofNullable(resource));
        when(userContext.getUserId()).thenReturn(userId);
        when(teamMemberRepository.findById(userId)).thenReturn(teamMember);

        resourceService.deleteResource(resourceId);

        verify(s3Service).deleteFile(resourceKey);
        verify(projectService).save(any(Project.class));
    }

    @Test
    void deleteResource_noPermissions_throwsSecurityException() {
        teamMember.setId(userId);
        teamMember.setId(invalidUserId);

        resource.setCreatedBy(teamMember);

        verify(s3Service, never()).deleteFile(resourceKey);
        verify(projectService, never()).save(any(Project.class));
    }

    @Test
    void downloadResource_validRequest_s3serviceCalled() {
        resource.setKey(resourceKey);
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.ofNullable(resource));

        resourceService.downloadResource(resourceId);

        verify(s3Service).downloadFile(any(String.class));
    }
}
