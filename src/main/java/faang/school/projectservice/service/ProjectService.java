package faang.school.projectservice.service;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.utils.ImageResizer;
import faang.school.projectservice.validation.ProjectCoverValidator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Сервис для управления обложками проектов.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final ProjectCoverValidator projectCoverValidator;
    private final ImageResizer imageResizer;

    /**
     * Загружает обложку для проекта.
     *
     * @param projectId идентификатор проекта
     * @param image     файл изображения для обложки
     */
    public void uploadCover(long projectId,
                            @NotNull MultipartFile image) {
        projectCoverValidator.validateBasics(image);

        if (projectCoverValidator.isImageOversize(image)) {
            image = imageResizer.resizeImage(image);
        }

        Project project = getProject(projectId);
        String oldKey = project.getCoverImageId();
        String folder = String.format("projects/%d/cover", projectId);

        String key = s3Service.uploadImage(folder, image);
        project.setCoverImageId(key);

        if (oldKey != null) {
            s3Service.deleteImage(oldKey);
        }
        projectRepository.save(project);
    }

    /**
     * Удаляет обложку проекта.
     *
     * @param projectId идентификатор проекта
     * @throws DataValidationException если у проекта нет обложки
     */
    public void deleteCover(long projectId) {
        Project project = getProject(projectId);
        String key = project.getCoverImageId();
        if (key == null) {
            log.error("Cover image id is null");
            throw new DataValidationException("Cover image id is null");
        }
        s3Service.deleteImage(key);
        project.setCoverImageId(null);
        projectRepository.save(project);
    }

    /**
     * Получает проект по идентификатору.
     *
     * @param projectId идентификатор проекта
     * @return найденный проект
     * @throws EntityNotFoundException если проект не найден
     */
    private Project getProject(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }
}
