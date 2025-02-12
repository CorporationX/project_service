package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectReport;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectReportRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectReportServiceTest {
    @Mock
    private ProjectReportRepository projectReportRepository;

    @Mock
    private PdfReportGeneratorService pdfReportGeneratorService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private ProjectReportService projectReportService;

    @Test
    void createProjectReportTest() {
        Long projectId = 1L;
        String fileName = "project_report_" + projectId + ".pdf";
        String dummyUrl = "https://dummy-url.com/" + fileName;

        Resource dummyResource = new ByteArrayResource("dummy pdf content".getBytes());
        Project project = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("This is a test project")
                .createdAt(LocalDateTime.now())
                .status(ProjectStatus.CREATED)
                .ownerId(1L)
                .teams(Collections.emptyList())
                .tasks(Collections.emptyList())
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.ofNullable(project));
        when(pdfReportGeneratorService.generateProjectReport(eq(project))).thenReturn(dummyResource);
        when(minioService.generatePresignedUrl(eq(fileName))).thenReturn(dummyUrl);
        when(projectReportRepository.save(any(ProjectReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectReport result = projectReportService.createProjectReport(projectId);

        verify(minioService).removePdfFromMinio(eq(fileName));
        verify(minioService).uploadPdfToMinio(any(), eq(fileName));
        verify(minioService).generatePresignedUrl(eq(fileName));
        verify(projectRepository).findById(eq(projectId));
        verify(pdfReportGeneratorService).generateProjectReport(eq(project));
        verify(projectReportRepository).save(any(ProjectReport.class));

        assertNotNull(result, "Returned ProjectReport should not be null");
        assertEquals(projectId, result.getProjectId(), "Project ID should match");
        assertEquals(fileName, result.getFileKey(), "File key should match");
        assertEquals(dummyUrl, result.getFileUrl(), "File URL should match");
    }

    @Test
    void testGetProjectReportFound() {
        Long projectId = 1L;
        ProjectReport report = new ProjectReport();
        report.setProjectId(projectId);
        report.setFileKey("project_report_1.pdf");

        when(projectReportRepository.getReportByProjectId(eq(projectId))).thenReturn(Optional.of(report));

        ProjectReport result = projectReportService.getProjectReport(projectId);

        assertNotNull(result);
        assertEquals(projectId, result.getProjectId());
        assertEquals("project_report_1.pdf", result.getFileKey());
    }

    @Test
    void testGetProjectReportNotFound() {
        Long projectId = 2L;
        when(projectReportRepository.getReportByProjectId(eq(projectId))).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            projectReportService.getProjectReport(projectId);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Project report not found", exception.getReason());
    }

    @Test
    void testGetUrlResourceValid() throws Exception {
        ProjectReport report = new ProjectReport();
        String validUrl = "http://dummy-url.com/project_report_1.pdf";
        report.setFileUrl(validUrl);

        Resource resource = projectReportService.getUrlResource(report);

        assertNotNull(resource);

        URI resourceUri = resource.getURI();
        assertEquals(URI.create(validUrl), resourceUri);
    }

    @Test
    void testGetUrlResourceMalformedUrl() {
        ProjectReport report = new ProjectReport();
        String invalidUrl = "ht@tp://invalid-url";
        report.setFileUrl(invalidUrl);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectReportService.getUrlResource(report);
        });
        assertTrue(exception.getMessage().contains(invalidUrl));
    }
}
