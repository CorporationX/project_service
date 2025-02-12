package faang.school.projectservice.controller;

import faang.school.projectservice.model.ProjectReport;
import faang.school.projectservice.service.ProjectReportService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reports")
public class ProjectReportController {
    private static final Logger log = LoggerFactory.getLogger(ProjectReportController.class);
    private final ProjectReportService projectReportService;

    @PostMapping("/generate/{projectId}")
    public ResponseEntity<Resource> generateReport(@PathVariable Long projectId) {
        log.info("Generating report for projectId: {}", projectId);
        ProjectReport projectReport = projectReportService.createProjectReport(projectId);
        Resource resource = projectReportService.getUrlResource(projectReport);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(projectReportService.getContentLength(resource))
                    .body(resource);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Resource> getReport(@PathVariable Long projectId) {
        log.info("Fetching report for projectId: {}", projectId);
        ProjectReport projectReport = projectReportService.getProjectReport(projectId);
        Resource resource = projectReportService.getUrlResource(projectReport);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(projectReportService.getContentLength(resource))
                    .body(resource);
    }
}
