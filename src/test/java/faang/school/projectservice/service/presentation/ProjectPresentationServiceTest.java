package faang.school.projectservice.service.presentation;

import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.presentation.ProjectPresentationDto;
import faang.school.projectservice.dto.resource.S3FileResponse;
import faang.school.projectservice.exeption.EntityNotFoundException;
import faang.school.projectservice.exeption.S3DownloadException;
import faang.school.projectservice.mapper.ProjectInfoMapper;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.mapper.TeamMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.presentation.generator.PdfGenerator;
import faang.school.projectservice.service.presentation.stats.ProjectPresentationStatsServiceImpl;
import faang.school.projectservice.service.presentation.uploader.UploaderService;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.FileKeyGenerator;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectPresentationServiceTest {

    @InjectMocks
    private ProjectPresentationServiceImpl presentationService;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectPresentationStatsServiceImpl statsService;

    @Mock
    private PdfGenerator pdfGenerator;

    @Mock
    private FileKeyGenerator fileKeyGenerator;

    @Mock
    private S3Service s3Service;

    @Mock
    private UploaderService uploaderService;

    @Mock
    private UserService userService;

    @Spy
    private ProjectInfoMapper infoMapper;

    @Spy
    private TeamMapper teamMapper;

    @Spy
    private TaskMapper taskMapper;

    private static final Long PROJECT_ID = 1L;
    private static final Long OWNER_ID = 10L;
    private static final String USERNAME = "testuser";
    private static final String EMAIL = "email@test.com";
    private static final String FILE_KEY = "presentation-key";

    @Test
    @DisplayName("Should generate and upload PDF presentation for given project")
    void shouldCreatePresentationSuccessfully() throws IOException {
        Project project = createProject(OWNER_ID);
        UserDto owner = createUserDto();
        File file = createTempFile();

        when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
        when(userService.getById(OWNER_ID)).thenReturn(owner);
        when(fileKeyGenerator.generateForProject(PROJECT_ID)).thenReturn(FILE_KEY);
        when(pdfGenerator.generateToFile(any(ProjectPresentationDto.class))).thenReturn(file);

        presentationService.create(PROJECT_ID);

        verify(projectService).getProjectById(PROJECT_ID);
        verify(userService).getById(OWNER_ID);
        verify(pdfGenerator).generateToFile(any(ProjectPresentationDto.class));
        verify(fileKeyGenerator).generateForProject(PROJECT_ID);
        verify(uploaderService).uploadPdf(project, file, FILE_KEY);
        verify(projectService).save(project);
        verify(statsService).calculate(project);

        assertEquals(FILE_KEY, project.getPresentationFileKey());
        assertNotNull(project.getPresentationGeneratedAt());
    }

    @Test
    @DisplayName("Should throw if project not found")
    void shouldThrowIfProjectNotFound() {
        when(projectService.getProjectById(PROJECT_ID))
                .thenThrow(new EntityNotFoundException("Project not found"));

        assertThrows(EntityNotFoundException.class, () -> presentationService.create(PROJECT_ID));
        verify(projectService).getProjectById(PROJECT_ID);
        verifyNoMoreInteractions(userService, pdfGenerator, uploaderService);
    }

    @Test
    @DisplayName("Should throw if project owner not found")
    void shouldThrowIfOwnerNotFound() {
        Long missingOwnerId = 99L;
        Project project = createProject(missingOwnerId);

        when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
        when(userService.getById(missingOwnerId))
                .thenThrow(new EntityNotFoundException("User not found"));

        assertThrows(EntityNotFoundException.class, () -> presentationService.create(PROJECT_ID));
        verify(projectService).getProjectById(PROJECT_ID);
        verify(userService).getById(missingOwnerId);
        verifyNoMoreInteractions(pdfGenerator, uploaderService);
    }

    @Test
    @DisplayName("Should throw if PDF generation fails")
    void shouldThrowIfPdfGenerationFails() {
        Project project = createProject(OWNER_ID);
        UserDto user = createUserDto();

        when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
        when(userService.getById(OWNER_ID)).thenReturn(user);
        when(pdfGenerator.generateToFile(any())).thenThrow(new RuntimeException("PDF generation error"));

        assertThrows(RuntimeException.class, () -> presentationService.create(PROJECT_ID));
        verify(pdfGenerator).generateToFile(any());
    }

    @Test
    @DisplayName("Should throw if uploading PDF fails")
    void shouldThrowIfUploadFails() throws IOException {
        Project project = createProject(OWNER_ID);
        UserDto user = createUserDto();
        File file = createTempFile();

        when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
        when(userService.getById(OWNER_ID)).thenReturn(user);
        when(fileKeyGenerator.generateForProject(PROJECT_ID)).thenReturn(FILE_KEY);
        when(pdfGenerator.generateToFile(any())).thenReturn(file);
        doThrow(new RuntimeException("S3 error"))
                .when(uploaderService).uploadPdf(project, file, FILE_KEY);

        assertThrows(RuntimeException.class, () -> presentationService.create(PROJECT_ID));
        verify(uploaderService).uploadPdf(project, file, FILE_KEY);
    }

    @Test
    @DisplayName("Should download generated presentation successfully")
    void shouldDownloadPresentationSuccessfully() {
        Project project = createProject(OWNER_ID);
        project.setPresentationFileKey(FILE_KEY);

        InputStream mockStream = new ByteArrayInputStream("PDF content".getBytes());

        when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
        when(s3Service.download(FILE_KEY)).thenReturn(mockStream);

        S3FileResponse response = presentationService.downloadPresentation(PROJECT_ID);

        assertNotNull(response);
        assertEquals(ProjectPresentationServiceImpl.PDF_FILE_NAME, response.fileName());
        assertEquals("pdf", response.contentType());
        assertNotNull(response.inputStream());

        verify(projectService).getProjectById(PROJECT_ID);
        verify(s3Service).download(FILE_KEY);
    }

    @Test
    @DisplayName("Should throw when presentation is not yet generated")
    void shouldThrowWhenPresentationNotGenerated() {
        Project project = createProject(OWNER_ID);
        project.setPresentationFileKey(null);

        when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);

        S3DownloadException exception = assertThrows(S3DownloadException.class,
                () -> presentationService.downloadPresentation(PROJECT_ID));

        assertEquals("Presentation not generated", exception.getMessage());
        verify(projectService).getProjectById(PROJECT_ID);
        verifyNoMoreInteractions(s3Service);
    }

    @Test
    @DisplayName("Should propagate exception when S3 download fails")
    void shouldThrowWhenS3DownloadFails() {
        Project project = createProject(OWNER_ID);
        project.setPresentationFileKey(FILE_KEY);

        when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
        when(s3Service.download(FILE_KEY)).thenThrow(new RuntimeException("S3 failure"));

        assertThrows(RuntimeException.class, () -> presentationService.downloadPresentation(PROJECT_ID));

        verify(projectService).getProjectById(PROJECT_ID);
        verify(s3Service).download(FILE_KEY);
    }

    private Project createProject(Long ownerId) {
        return Project.builder()
                .id(PROJECT_ID)
                .ownerId(ownerId)
                .name("Test Project")
                .createdAt(LocalDateTime.now().minusDays(7))
                .updatedAt(LocalDateTime.now())
                .teams(List.of())
                .tasks(List.of())
                .build();
    }

    private UserDto createUserDto() {
        return new UserDto(OWNER_ID, USERNAME, EMAIL);
    }

    private File createTempFile() throws IOException {
        return File.createTempFile("test", ".pdf");
    }
}
