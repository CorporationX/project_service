package faang.school.projectservice.service.presentation;

import faang.school.projectservice.exception.presentation.ProjectNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PresentationService {

    private final ProjectRepository projectRepository;
    private final PresentationPdfGenerator pdfGenerator;
    private final FileStorageService fileStorageService;

    public String generateAndUploadPresentation(Long projectId) throws Exception {
        log.info("Generating presentation PDF for project id {}", projectId);
        Project project = fetchProject(projectId);
        byte[] pdfPresentation = pdfGenerator.generatePdf(project);
        log.info("Uploading generated presentation to Minio");
        String fileKey = fileStorageService.uploadFileToMinio(pdfPresentation);
        log.info("Presentation uploaded with file key: {}", fileKey);
        project.setPresentationFileKey(fileKey);
        projectRepository.save(project);
        return fileKey;
    }

    public byte[] downloadPresentation(String fileKey) {
        return fileStorageService.downloadPresentationFromMinio(fileKey);
    }

    private Project fetchProject(Long projectId) throws ProjectNotFoundException {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isEmpty()) {
            log.error("Project with ID {} not found", projectId);
            throw new ProjectNotFoundException("Project with ID " + projectId + " not found");
        }
        log.info("Project with ID {} has been successfully fetched", projectId);
        return optionalProject.get();
    }
}
