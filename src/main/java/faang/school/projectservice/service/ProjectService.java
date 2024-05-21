package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.image.ImageService;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final ImageService imageService;

    public Project findById(Long id) {
        return projectRepository.getProjectById(id);
    }

    @Transactional
    public void uploadFile(MultipartFile file, Long projectId, String folder) {
        Project project = projectRepository.getProjectById(projectId);
        log.info("Image preprocessing");
        file = imageService.imageProcessing(file);
        String imageId = s3Service.uploadFile(file, folder);
        project.setCoverImageId(imageId);
        projectRepository.save(project);
    }
}
