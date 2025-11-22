package faang.school.projectservice.service;

import faang.school.projectservice.dto.FileDownloadResponse;
import faang.school.projectservice.enums.Role;
import faang.school.projectservice.exception.FileStorageException;
import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.exception.StorageLimitExceededException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigInteger;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileStorageService Unit Tests")
class FileStorageServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private FileStorageService fileStorageService;

    private static final String BUCKET_NAME = "test-bucket";
    
    private Project testProject;
    private TeamMember testTeamMember;
    private Team testTeam;
    private Resource testResource;
    private MockMultipartFile testFile;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileStorageService, "bucketName", BUCKET_NAME);
        
        testProject = Project.builder()
                .id(1L)
                .name("Test Project")
                .storageSize(BigInteger.ZERO)
                .maxStorageSize(BigInteger.valueOf(2_147_483_648L)) // 2GB
                .status(ProjectStatus.ACTIVE)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        
        testTeam = Team.builder()
                .id(1L)
                .project(testProject)
                .build();
        
        testTeamMember = TeamMember.builder()
                .id(1L)
                .userId(100L)
                .nickname("Test User")
                .team(testTeam)
                .roles(List.of(TeamRole.DEVELOPER, TeamRole.MANAGER))
                .build();
        
        testResource = Resource.builder()
                .id(1L)
                .name("test-file.pdf")
                .key("project-1/12345-uuid-test-file.pdf")
                .size(BigInteger.valueOf(1024L))
                .contentType("application/pdf")
                .type(ResourceType.PDF)
                .status(ResourceStatus.ACTIVE)
                .project(testProject)
                .createdBy(testTeamMember)
                .allowedRoles(List.of(TeamRole.DEVELOPER))
                .build();
        
        testFile = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "Test PDF content".getBytes()
        );
    }
    
    @Nested
    @DisplayName("Upload File Tests")
    class UploadFileTests {
        
        @Test
        @DisplayName("Should successfully upload file")
        void shouldUploadFileSuccessfully() throws Exception {
            // Given
            when(projectRepository.findByIdWithLock(1L))
                    .thenReturn(Optional.of(testProject));
            when(teamMemberRepository.findById(1L))
                    .thenReturn(Optional.of(testTeamMember));
            when(resourceRepository.save(any(Resource.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(resourceRepository.calculateProjectStorageSize(1L))
                    .thenReturn(testFile.getSize());
            doAnswer(invocation -> null).when(minioClient).putObject(any(PutObjectArgs.class));
            when(projectRepository.updateStorageSize(anyLong(), anyLong())).thenReturn(1);
            
            // When
            Resource result = fileStorageService.uploadFile(
                    testFile, 1L, 1L, Set.of(Role.DEVELOPER)
            );
            
            // Then
            assertNotNull(result);
            assertEquals("test-document.pdf", result.getName());
            assertEquals(ResourceStatus.ACTIVE, result.getStatus());
            assertNotNull(result.getKey());
            
            // Verify MinIO upload
            verify(minioClient).putObject(any(PutObjectArgs.class));
            
            // Verify database operations
            verify(resourceRepository).save(any(Resource.class));
            verify(projectRepository).updateStorageSize(eq(1L), anyLong());
        }
        
        @Test
        @DisplayName("Should fail when file is empty")
        void shouldFailWhenFileIsEmpty() {
            // Given
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "file", "empty.txt", "text/plain", new byte[0]
            );
            
            // When & Then
            assertThrows(FileStorageException.class, () ->
                    fileStorageService.uploadFile(emptyFile, 1L, 1L, null)
            );
            
            verifyNoInteractions(minioClient);
            verifyNoInteractions(resourceRepository);
        }
        
        @Test
        @DisplayName("Should fail when file exceeds size limit")
        void shouldFailWhenFileExceedsSizeLimit() {
            // Given
            MockMultipartFile largeFile = new MockMultipartFile(
                    "file",
                    "large.zip",
                    "application/zip",
                    new byte[500_000_001] // 500MB + 1 byte
            );
            
            // When & Then
            FileStorageException exception = assertThrows(
                    FileStorageException.class, 
                    () -> fileStorageService.uploadFile(largeFile, 1L, 1L, null)
            );
            
            assertTrue(exception.getMessage().contains("exceeds maximum allowed size"));
        }
        
        @Test
        @DisplayName("Should fail when storage limit exceeded")
        void shouldFailWhenStorageLimitExceeded() {
            // Given
            testProject.setStorageSize(BigInteger.valueOf(2_147_483_640L)); // Almost 2GB
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", new byte[1024 * 1024] // 1MB
            );
            
            when(projectRepository.findByIdWithLock(1L))
                    .thenReturn(Optional.of(testProject));
            
            // When & Then
            assertThrows(StorageLimitExceededException.class, () ->
                    fileStorageService.uploadFile(file, 1L, 1L, null)
            );
        }
        
        @Test
        @DisplayName("Should reject blocked file extensions")
        void shouldRejectBlockedExtensions() {
            // Given
            MockMultipartFile executableFile = new MockMultipartFile(
                    "file", "malware.exe", "application/x-msdownload", 
                    "dangerous content".getBytes()
            );
            
            // When & Then
            assertThrows(FileStorageException.class, () ->
                    fileStorageService.uploadFile(executableFile, 1L, 1L, null)
            );
        }
        
        @Test
        @DisplayName("Should use team member roles when allowedRoles is null")
        void shouldUseTeamMemberRolesWhenAllowedRolesNull() throws Exception {
            // Given
            when(projectRepository.findByIdWithLock(1L))
                    .thenReturn(Optional.of(testProject));
            when(teamMemberRepository.findById(1L))
                    .thenReturn(Optional.of(testTeamMember));
            when(resourceRepository.save(any(Resource.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(resourceRepository.calculateProjectStorageSize(1L))
                    .thenReturn(testFile.getSize());
            doAnswer(invocation -> null).when(minioClient).putObject(any(PutObjectArgs.class));
            when(projectRepository.updateStorageSize(anyLong(), anyLong())).thenReturn(1);
            
            // When
            Resource result = fileStorageService.uploadFile(testFile, 1L, 1L, null);
            
            // Then
            ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);
            verify(resourceRepository).save(resourceCaptor.capture());
            
            Resource savedResource = resourceCaptor.getValue();
            assertEquals(testTeamMember.getRoles(), savedResource.getAllowedRoles());
        }
        
        @Test
        @DisplayName("Should fail when project not found")
        void shouldFailWhenProjectNotFound() {
            // Given
            when(projectRepository.findByIdWithLock(1L))
                    .thenReturn(Optional.empty());
            
            // When & Then
            assertThrows(ResourceNotFoundException.class, () ->
                    fileStorageService.uploadFile(testFile, 1L, 1L, null)
            );
        }
        
        @Test
        @DisplayName("Should fail when team member not found")
        void shouldFailWhenTeamMemberNotFound() {
            // Given
            when(projectRepository.findByIdWithLock(1L))
                    .thenReturn(Optional.of(testProject));
            when(teamMemberRepository.findById(1L))
                    .thenReturn(Optional.empty());
            
            // When & Then
            assertThrows(ResourceNotFoundException.class, () ->
                    fileStorageService.uploadFile(testFile, 1L, 1L, null)
            );
        }
    }
    
    @Nested
    @DisplayName("Download File Tests")
    class DownloadFileTests {
        
        @Test
        @DisplayName("Should successfully download file")
        void shouldDownloadFileSuccessfully() throws Exception {
            // Given
            InputStream mockInputStream = new ByteArrayInputStream("file content".getBytes());
            GetObjectResponse mockResponse = mock(GetObjectResponse.class);
            
            when(resourceRepository.findByIdAndProjectId(1L, 1L))
                    .thenReturn(Optional.of(testResource));
            when(teamMemberRepository.findByUserIdAndProjectId(100L, 1L))
                    .thenReturn(Optional.of(testTeamMember));
            doAnswer(invocation -> mockResponse).when(minioClient)
                    .getObject(any(GetObjectArgs.class));
            
            // When
            FileDownloadResponse result = fileStorageService.downloadFile(1L, 1L, 100L);
            
            // Then
            assertNotNull(result);
            assertEquals("test-file.pdf", result.getFileName());
            assertEquals("application/pdf", result.getContentType());
            assertEquals(1024L, result.getSize());
            assertNotNull(result.getInputStream());
            
            verify(minioClient).getObject(any(GetObjectArgs.class));
        }
        
        @Test
        @DisplayName("Should fail when resource not found")
        void shouldFailWhenResourceNotFound() {
            // Given
            when(resourceRepository.findByIdAndProjectId(999L, 1L))
                    .thenReturn(Optional.empty());
            
            // When & Then
            assertThrows(ResourceNotFoundException.class, () ->
                    fileStorageService.downloadFile(999L, 1L, 100L)
            );
        }
        
        @Test
        @DisplayName("Should fail when resource is deleted")
        void shouldFailWhenResourceIsDeleted() {
            // Given
            testResource.setStatus(ResourceStatus.DELETED);
            when(resourceRepository.findByIdAndProjectId(1L, 1L))
                    .thenReturn(Optional.of(testResource));
            when(teamMemberRepository.findByUserIdAndProjectId(100L, 1L))
                    .thenReturn(Optional.of(testTeamMember));
            
            // When & Then
            assertThrows(FileStorageException.class, () ->
                    fileStorageService.downloadFile(1L, 1L, 100L)
            );
        }
        
        @Test
        @DisplayName("Should fail when user has no access")
        void shouldFailWhenUserHasNoAccess() {
            // Given
            TeamMember unauthorizedMember = TeamMember.builder()
                    .id(2L)
                    .userId(200L)
                    .nickname("Unauthorized")
                    .roles(List.of(TeamRole.TESTER))
                    .build();
            
            when(resourceRepository.findByIdAndProjectId(1L, 1L))
                    .thenReturn(Optional.of(testResource));
            when(teamMemberRepository.findByUserIdAndProjectId(200L, 1L))
                    .thenReturn(Optional.of(unauthorizedMember));
            
            // When & Then
            assertThrows(FileStorageException.class, () ->
                    fileStorageService.downloadFile(1L, 1L, 200L)
            );
        }
    }
    
    @Nested
    @DisplayName("Delete File Tests")
    class DeleteFileTests {
        
        @Test
        @DisplayName("Should successfully delete file by owner")
        void shouldDeleteFileByOwner() throws Exception {
            // Given
            testResource.setCreatedBy(testTeamMember);
            when(resourceRepository.findByIdAndProjectId(1L, 1L))
                    .thenReturn(Optional.of(testResource));
            when(teamMemberRepository.findById(1L))
                    .thenReturn(Optional.of(testTeamMember));
            when(resourceRepository.save(any(Resource.class)))
                    .thenReturn(testResource);
            when(resourceRepository.calculateProjectStorageSize(1L))
                    .thenReturn(0L);
            doAnswer(invocation -> null).when(minioClient).removeObject(any(RemoveObjectArgs.class));
            when(projectRepository.updateStorageSize(anyLong(), anyLong())).thenReturn(1);
            
            // When
            fileStorageService.deleteFile(1L, 1L, 1L);
            
            // Then
            verify(minioClient).removeObject(any(RemoveObjectArgs.class));
            
            ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);
            verify(resourceRepository).save(resourceCaptor.capture());
            
            Resource deletedResource = resourceCaptor.getValue();
            assertNull(deletedResource.getKey());
            assertEquals(BigInteger.ZERO, deletedResource.getSize());
            assertEquals(ResourceStatus.DELETED, deletedResource.getStatus());
            assertEquals(testTeamMember, deletedResource.getUpdatedBy());
        }
        
        @Test
        @DisplayName("Should allow manager to delete any file")
        void shouldAllowManagerToDeleteAnyFile() throws Exception {
            // Given
            TeamMember managerMember = TeamMember.builder()
                    .id(2L)
                    .userId(200L)
                    .nickname("Manager")
                    .roles(List.of(TeamRole.MANAGER))
                    .build();
            
            testResource.setCreatedBy(testTeamMember); // Different creator
            
            when(resourceRepository.findByIdAndProjectId(1L, 1L))
                    .thenReturn(Optional.of(testResource));
            when(teamMemberRepository.findById(2L))
                    .thenReturn(Optional.of(managerMember));
            when(resourceRepository.save(any(Resource.class)))
                    .thenReturn(testResource);
            when(resourceRepository.calculateProjectStorageSize(1L))
                    .thenReturn(0L);
            doAnswer(invocation -> null).when(minioClient).removeObject(any(RemoveObjectArgs.class));
            when(projectRepository.updateStorageSize(anyLong(), anyLong())).thenReturn(1);
            
            // When
            fileStorageService.deleteFile(1L, 1L, 2L);
            
            // Then
            verify(minioClient).removeObject(any(RemoveObjectArgs.class));
            verify(resourceRepository).save(any(Resource.class));
        }
        
        @Test
        @DisplayName("Should fail when non-owner non-manager tries to delete")
        void shouldFailWhenUnauthorizedDelete() throws Exception {
            // Given
            TeamMember otherMember = TeamMember.builder()
                    .id(2L)
                    .userId(200L)
                    .nickname("Other")
                    .roles(List.of(TeamRole.DEVELOPER))
                    .build();
            
            testResource.setCreatedBy(testTeamMember); // Different creator
            
            when(resourceRepository.findByIdAndProjectId(1L, 1L))
                    .thenReturn(Optional.of(testResource));
            when(teamMemberRepository.findById(2L))
                    .thenReturn(Optional.of(otherMember));
            
            // When & Then
            assertThrows(FileStorageException.class, () ->
                    fileStorageService.deleteFile(1L, 1L, 2L)
            );
            
            verify(minioClient, never()).removeObject(any());
            verify(resourceRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("Should handle already deleted resource")
        void shouldHandleAlreadyDeletedResource() throws Exception {
            // Given
            testResource.setStatus(ResourceStatus.DELETED);
            when(resourceRepository.findByIdAndProjectId(1L, 1L))
                    .thenReturn(Optional.of(testResource));
            when(teamMemberRepository.findById(1L))
                    .thenReturn(Optional.of(testTeamMember));
            
            // When
            fileStorageService.deleteFile(1L, 1L, 1L);
            
            // Then
            verify(minioClient, never()).removeObject(any());
            verify(resourceRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("Generate Presigned URL Tests")
    class PresignedUrlTests {
        
        @Test
        @DisplayName("Should generate presigned URL successfully")
        void shouldGeneratePresignedUrl() throws Exception {
            // Given
            String expectedUrl = "https://minio.example.com/bucket/file?signature=abc123";
            
            when(resourceRepository.findByIdAndProjectId(1L, 1L))
                    .thenReturn(Optional.of(testResource));
            when(teamMemberRepository.findByUserIdAndProjectId(100L, 1L))
                    .thenReturn(Optional.of(testTeamMember));
            doAnswer(invocation -> expectedUrl).when(minioClient)
                    .getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
            
            // When
            String result = fileStorageService.generatePresignedUrl(1L, 1L, 100L);
            
            // Then
            assertEquals(expectedUrl, result);
            verify(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
        }
        
        @Test
        @DisplayName("Should fail when resource not found")
        void shouldFailWhenResourceNotFound() {
            // Given
            when(resourceRepository.findByIdAndProjectId(999L, 1L))
                    .thenReturn(Optional.empty());
            
            // When & Then
            assertThrows(ResourceNotFoundException.class, () ->
                    fileStorageService.generatePresignedUrl(999L, 1L, 100L)
            );
        }
    }
}

