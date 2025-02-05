package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.s3.S3Service;
import faang.school.projectservice.service.imageprocessing.ImageProcessingUtils;
import faang.school.projectservice.validator.project.FileValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final ProjectMapper projectMapper;
    private final FileValidator validator;
    private final ImageProcessingUtils imageProcessingUtils;

    public Project getProject(Long id) {
        return getProjectById(id);
    }

    public ProjectDto addProjectCover(Long projectId, MultipartFile file) {
        Project project = getProjectById(projectId);

        validator.validateFile(file);
        validator.checkFileSize(file.getSize());

        MultipartFile resizedImageBytes = imageProcessingUtils.convertByteToMultipartFile(
                imageProcessingUtils.resizeImage(file),
                file.getName(),
                file.getContentType()
        );

        String folder = project.getId() + project.getName();
        String key = s3Service.uploadFile(folder, resizedImageBytes);
        project.setCoverImageId(key);
        Project updatedProject = projectRepository.save(project);
        log.info("Обложка успешно добавлена для проекта с id={}", projectId);
        return projectMapper.toDto(updatedProject);
    }

    public ProjectDto deleteProjectCover(Long projectId) {
        Project project = getProjectById(projectId);
        s3Service.deleteFile(project.getCoverImageId());
        project.setCoverImageId(null);
        Project updatedProject = projectRepository.save(project);
        log.info("Обложка успешно удалена для проекта с id={}", projectId);
        return projectMapper.toDto(updatedProject);
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).
                orElseThrow(() -> new EntityNotFoundException("Проект с id=" + projectId + " не найден"));
    }
}
