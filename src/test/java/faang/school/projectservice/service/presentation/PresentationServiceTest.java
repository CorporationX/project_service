package faang.school.projectservice.service.presentation;

import faang.school.projectservice.exception.presentation.ProjectNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresentationServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private PresentationPdfGenerator pdfGenerator;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private PresentationService presentationService;

    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
    }

    @Test
    void testGenerateAndUploadPresentation_Success() throws Exception {
        byte[] pdfBytes = "dummy pdf content".getBytes();
        String expectedFileKey = "file-key-123";

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(pdfGenerator.generatePdf(project)).thenReturn(pdfBytes);
        when(fileStorageService.uploadFileToMinio(pdfBytes)).thenReturn(expectedFileKey);

        String resultFileKey = presentationService.generateAndUploadPresentation(1L);

        assertEquals(expectedFileKey, resultFileKey);
        assertEquals(expectedFileKey, project.getPresentationFileKey());
        verify(projectRepository).save(project);
    }

    @Test
    void testGenerateAndUploadPresentation_ProjectNotFound() {
        when(projectRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ProjectNotFoundException.class, () ->
                presentationService.generateAndUploadPresentation(2L));
        assertTrue(exception.getMessage().contains("Project with ID 2 not found"));
    }

    @Test
    void testDownloadPresentation() {
        byte[] expectedBytes = "dummy pdf content".getBytes();
        String fileKey = "file-key-123";

        when(fileStorageService.downloadPresentationFromMinio(fileKey)).thenReturn(expectedBytes);

        byte[] actualBytes = presentationService.downloadPresentation(fileKey);
        assertArrayEquals(expectedBytes, actualBytes);
    }
}