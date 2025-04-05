package faang.school.projectservice.service.projectresource;

import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ResourceHandlingException;
import faang.school.projectservice.exception.StorageSizeExceededException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.service.tika.TikaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private S3Service s3Service;
    @Mock
    private ResourceMapper resourceMapper;
    @Mock
    private AmazonS3 amazonS3;
    @Mock
    private TikaService tikaService;

    @InjectMocks
    private ProjectResourceServiceImpl projectResourceService;

    private Project project;
    private TeamMember teamMember;
    private Resource resource;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .storageSize(BigInteger.valueOf(100))
                .maxStorageSize(BigInteger.valueOf(1000))
                .build();

        teamMember = TeamMember.builder()
                .id(1L)
                .roles(List.of(TeamRole.MANAGER))
                .build();

        resource = Resource.builder()
                .id(1L)
                .name("test.txt")
                .size(BigInteger.valueOf(100))
                .status(ResourceStatus.ACTIVE)
                .project(project)
                .createdBy(teamMember)
                .build();

        file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );
    }

    @Test
    void testUploadFileSuccess() throws IOException {

        project.setStorageSize(BigInteger.valueOf(100));
        when(projectService.getProjectById(anyLong())).thenReturn(project);
        when(teamMemberService.getCurrentTeamMember(anyLong())).thenReturn(teamMember);
        doNothing().when(s3Service).uploadFile(any(), any());
        when(resourceRepository.save(any())).thenReturn(resource);
        when(tikaService.detectMimeType(any(MultipartFile.class))).thenReturn("text/plain");
        ResourceDto expectedDto = ResourceDto.builder()
                .id(1L)
                .name("test.txt")
                .size(BigInteger.valueOf(100))
                .projectId(1L)
                .build();
        when(resourceMapper.toDto(any())).thenReturn(expectedDto);
        ReflectionTestUtils.setField(projectResourceService, "maxStorageSize", 1000L);

        ResourceDto result = projectResourceService.uploadFile(1L, file);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(s3Service, times(1)).uploadFile(any(), any());
        verify(resourceRepository, times(1)).save(any());
        verify(tikaService, times(1)).detectMimeType(any(MultipartFile.class));
    }

    @Test
    void testUploadFileStorageExceeded() throws IOException {
        project.setStorageSize(BigInteger.valueOf(1000));
        project.setMaxStorageSize(BigInteger.valueOf(1000));
        when(projectService.getProjectById(anyLong())).thenReturn(project);
        when(teamMemberService.getCurrentTeamMember(anyLong())).thenReturn(teamMember);

        ReflectionTestUtils.setField(projectResourceService, "maxStorageSize", 1000L);

        assertThrows(StorageSizeExceededException.class,
                () -> projectResourceService.uploadFile(1L, file));
        verify(s3Service, never()).uploadFile(any(), any());
    }

    @Test
    void testDownloadFileSuccess() throws ResourceHandlingException {
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(new ByteArrayInputStream("Test".getBytes()));

        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));
        when(s3Service.downloadFile(any())).thenReturn(s3Object);

        InputStream result = projectResourceService.downloadFile(1L);

        assertNotNull(result);
        verify(resourceRepository, times(1)).findById(1L);
        verify(s3Service, times(1)).downloadFile(any());
    }

    @Test
    void testDownloadFileResourceNotFound() throws ResourceHandlingException {
        when(resourceRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> projectResourceService.downloadFile(1L));

        verify(s3Service, never()).downloadFile(any());
    }

    @Test
    void testDeleteFileSuccess() throws AccessDeniedException, ResourceHandlingException {
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));
        when(teamMemberService.getCurrentTeamMember(anyLong())).thenReturn(teamMember);

        projectResourceService.deleteFile(1L);

        verify(s3Service, times(1)).deleteFile(any());
        verify(resourceRepository, times(1)).delete(any());
    }

    @Test
    void testDeleteFileNoPermission() throws AccessDeniedException {
        TeamMember anotherMember = TeamMember.builder()
                .id(2L)
                .roles(List.of(TeamRole.DEVELOPER))
                .build();

        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));
        when(teamMemberService.getCurrentTeamMember(anyLong())).thenReturn(anotherMember);

        assertThrows(DataValidationException.class,
                () -> projectResourceService.deleteFile(1L));
    }
}