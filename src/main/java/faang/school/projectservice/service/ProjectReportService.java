package faang.school.projectservice.service;

import faang.school.projectservice.exseption.ErrorReadingFile;
import faang.school.projectservice.exseption.ProjectNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectReport;
import faang.school.projectservice.repository.ProjectReportRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;

@RequiredArgsConstructor
@Service
public class ProjectReportService {
    private final MinioService minioService;
    private final ProjectReportRepository projectReportRepository;
    private final PdfReportGeneratorService pdfReportGeneratorService;
    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectReport createProjectReport(Long projectId) {
        String fileName = "project_report_" + projectId + ".pdf";
        Boolean removeFile = minioService.removePdfFromMinio(fileName);
        if (removeFile){
            projectReportRepository.deleteReportByProjectId(projectId);
        }
        Resource pdfResource = generateAndUploadProjectReport(projectId);
        try {
            minioService.uploadPdfToMinio(pdfResource.getInputStream(), fileName);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading PDF to Minio for project " + projectId, e);
        }

        String fileUrl = minioService.generatePresignedUrl(fileName);

        ProjectReport projectReport = new ProjectReport();
        projectReport.setProjectId(projectId);
        projectReport.setFileKey(fileName);
        projectReport.setFileUrl(fileUrl);

        projectReportRepository.save(projectReport);
        return projectReport;
    }

    private Resource generateAndUploadProjectReport(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        return pdfReportGeneratorService.generateProjectReport(project);
    }

    public ProjectReport getProjectReport(Long projectId) {
        return projectReportRepository.getReportByProjectId(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project report not found"));
    }

    public Resource getUrlResource(ProjectReport projectReport) {
        String fileUrl = projectReport.getFileUrl();

        URI fileUri = URI.create(fileUrl);
        Resource resource;
        try {
            resource = new UrlResource(fileUri);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error URL file: {} " + fileUri, e);
        }
        return resource;
    }

    public long getContentLength(Resource resource) {
        try {
            return resource.contentLength();
        } catch (IOException e) {
            throw new ErrorReadingFile("Error reading content length", e);
        }
    }
}
