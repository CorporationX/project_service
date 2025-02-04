package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.StorageException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.utils.ResourcePrepareData.getDeletedResource;
import static faang.school.projectservice.utils.ResourcePrepareData.getProject;
import static faang.school.projectservice.utils.ResourcePrepareData.getProjectResult;
import static faang.school.projectservice.utils.ResourcePrepareData.getResource;
import static faang.school.projectservice.utils.ResourcePrepareData.getResultResource;
import static faang.school.projectservice.utils.ResourcePrepareData.getTeamMember;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceImplTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Spy
    private ResourceMapper resourceMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    public void testUploadFile() {
        when(projectRepository.findById(eq(1L))).thenReturn(
                Optional.of(getProject()));
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(s3Service.uploadFile(eq(multipartFile), eq("project")))
                .thenReturn(getResource());
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberRepository.findByUserIdAndProjectId(eq(1L), eq(1L)))
                .thenReturn(getTeamMember());
        when(resourceRepository.save(eq(getResultResource())))
                .thenReturn(getResultResource());
        when(projectRepository.save(any())).thenReturn(getProjectResult());

        ResourceDto resourceDto = resourceService.uploadFile(1L, multipartFile);

        assertEquals(resourceMapper.toDto(getResource()), resourceDto);
    }

    @Test
    public void testUploadFileWithStorageException() {
        when(projectRepository.findById(eq(1L))).thenReturn(
                Optional.of(getProject()));
        byte[] bytes = new byte[1024 * 1024 * 10];
        MockMultipartFile file = new MockMultipartFile("data", "file1.txt", "text/plain", bytes);

        assertThrows(StorageException.class, () -> resourceService.uploadFile(1L, file));
    }

    @Test
    public void testDownloadFile() {
        when(resourceRepository.findByKey(eq("key"))).thenReturn(Optional.ofNullable(getResource()));
        when(s3Service.downloadFile("key")).thenReturn(
                new ByteArrayInputStream("text".getBytes()));

        InputStream inputStream = resourceService.downloadFile("key");

        assertNotNull(inputStream);
    }

    @Test
    public void testDeleteFile() {
        when(resourceRepository.findByKey(eq("key"))).thenReturn(Optional.ofNullable(getResource()));
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberRepository.findByUserIdAndProjectId(eq(1L), eq(1L))).thenReturn(
                TeamMember.builder()
                        .id(1L)
                        .roles(List.of(TeamRole.ANALYST))
                        .build()
        );
        doNothing().when(s3Service).deleteFile("key");
        when(resourceRepository.save(any())).thenReturn(getDeletedResource());

        resourceService.deleteFile("key");

        verify(resourceRepository).findByKey(eq("key"));
    }

    @Test
    public void testDeleteFileWhenAccessDeniedException() {
        when(resourceRepository.findByKey(eq("key"))).thenReturn(Optional.ofNullable(getResource()));
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberRepository.findByUserIdAndProjectId(eq(1L), eq(1L))).thenReturn(
                TeamMember.builder()
                        .id(1L)
                        .roles(List.of(TeamRole.DEVELOPER))
                        .build()
        );

        assertThrows(AccessDeniedException.class, () -> resourceService.deleteFile("key"));
    }
}