package faang.school.projectservice.service.coverservice;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.CoverForProjectService.ProjectCoverService;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoverServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private UserContext userContext;
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private ProjectCoverService projectCoverService;

    private final long PROJECT_ID = 1L;
    private final long USER_ID = 1L;
    private final long OTHER_USER_ID = 2L;
    private final String S3_KEY = "12345 - test.jpg";
    private static final String OLD_S3_KEY = " 123 - хрень.jpg";

    private Project project;
    private MultipartFile validFile;
    private MultipartFile invalidFile;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(PROJECT_ID)
                .ownerId(USER_ID)
                .build();

        String FILE_NAME = "test.jpg";

        validFile = new MockMultipartFile(
                "file",
                FILE_NAME,
                "image/jpeg",
                "test image content".getBytes()
        );

        invalidFile = new MockMultipartFile(
                "file",
                FILE_NAME,
                "text/plain",
                "invalid content".getBytes()
        );
    }

    @Test
    void testAddImageToProjectSuccess() {

        UserDto userDto = new UserDto(USER_ID, "testuser", "user@example.com");
        Project savedProject = Project.builder()
                .id(PROJECT_ID)
                .ownerId(USER_ID)
                .coverImageId(S3_KEY)
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        when(s3Service.uploadFile(anyString(), any(MultipartFile.class))).thenReturn(S3_KEY);

        Project result = projectCoverService.addImageToProject(PROJECT_ID, validFile);

        assertNotNull(result, "Метод должен возвращать не-null Project");
        assertEquals(S3_KEY, result.getCoverImageId(), "Некорректный coverImageId");
        verify(s3Service).uploadFile(anyString(), eq(validFile));
        verify(projectRepository).save(project);
    }

    @Test
    void testReplaceExistingCoverSuccess() {

        project.setCoverImageId(OLD_S3_KEY);
        UserDto userDto = new UserDto(USER_ID, "testuser", "user@example.com");
        Project savedProject = Project.builder()
                .id(PROJECT_ID)
                .ownerId(USER_ID)
                .coverImageId(S3_KEY)
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        when(s3Service.uploadFile(anyString(), any(MultipartFile.class))).thenReturn(S3_KEY);

        Project result = projectCoverService.addImageToProject(PROJECT_ID, validFile);

        assertNotNull(result);
        assertEquals(S3_KEY, result.getCoverImageId());
        verify(s3Service).deleteFile(OLD_S3_KEY);
        verify(s3Service).uploadFile(anyString(), eq(validFile));
    }

    @Test
    void testAddImageToProjectWhenProjectNotFound() {

        UserDto userDto = new UserDto(USER_ID, "tes-tuser", "user@example.com");
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> projectCoverService.addImageToProject(PROJECT_ID, validFile));
    }

    @Test
    void testAddImageToProjectWhenNotOwner() {

        UserDto otherUser = new UserDto(OTHER_USER_ID, "other-user", "other@example.com");
        when(userContext.getUserId()).thenReturn(OTHER_USER_ID);
        when(userServiceClient.getUser(OTHER_USER_ID)).thenReturn(otherUser);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        assertThrows(DataValidationException.class,
                () -> projectCoverService.addImageToProject(PROJECT_ID, validFile));
    }

    @Test
    void testAddImageToProjectWhenInvalidFileType() {
        assertThrows(DataValidationException.class,
                () -> projectCoverService.addImageToProject(PROJECT_ID, invalidFile));
    }

    @Test
    void testRemoveCoverFromProjectByIdSuccess() {
        project.setCoverImageId(S3_KEY);
        UserDto userDto = new UserDto(USER_ID, "test-user", "user@example.com");
        Project savedProject = Project.builder()
                .id(PROJECT_ID)
                .ownerId(USER_ID)
                .coverImageId(null)
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        projectCoverService.removeCoverFromProjectById(PROJECT_ID);

        verify(s3Service).deleteFile(S3_KEY);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testRemoveCoverFromProjectByIdS3DeleteFails() {

        project.setCoverImageId(S3_KEY);
        UserDto userDto = new UserDto(USER_ID, "test-user", "user@example.com");
        Project savedProject = Project.builder()
                .id(PROJECT_ID)
                .ownerId(USER_ID)
                .coverImageId(null)
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        doThrow(new RuntimeException("S3 error")).when(s3Service).deleteFile(S3_KEY);

        projectCoverService.removeCoverFromProjectById(PROJECT_ID);

        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testRemoveCoverFromProjectByIdWhenProjectNotFound() {

        UserDto userDto = new UserDto(USER_ID, "test-user", "user@example.com");
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> projectCoverService.removeCoverFromProjectById(PROJECT_ID));
    }

    @Test
    void testRemoveCoverFromProjectByIdNotOwner() {

        UserDto otherUser = new UserDto(OTHER_USER_ID, "other-user", "other@example.com");
        when(userContext.getUserId()).thenReturn(OTHER_USER_ID);
        when(userServiceClient.getUser(OTHER_USER_ID)).thenReturn(otherUser);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        assertThrows(DataValidationException.class,
                () -> projectCoverService.removeCoverFromProjectById(PROJECT_ID));
    }

    @Test
    void testRemoveCoverFromProjectByIdNoCoverExists() {

        UserDto userDto = new UserDto(USER_ID, "test-user", "user@example.com");
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));


        assertThrows(DataValidationException.class,
                () -> projectCoverService.removeCoverFromProjectById(PROJECT_ID));
    }
}
