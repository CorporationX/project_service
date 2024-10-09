package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;

    private final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Transactional
    public String uploadCover(Long projectId, MultipartFile file) {
        checkProjectExist(projectId);
        checkFileSize(file);
        checkImageFile(file);

        String key = UUID.randomUUID().toString();
        s3Service.uploadFile(file, key);
        Project project = projectRepository.getProjectById(projectId);
        String oldCoverImageId = project.getCoverImageId();
        if (oldCoverImageId != null) {
            s3Service.deleteFile(oldCoverImageId);
        }
        project.setCoverImageId(key);
        projectRepository.save(project);

        return key;
    }

    @Transactional
    public void removeCover(Long projectId) {
        checkProjectExist(projectId);

        Project project = projectRepository.getProjectById(projectId);
        String coverImageId = project.getCoverImageId();
        if (coverImageId != null) {
            s3Service.deleteFile(coverImageId);
            project.setCoverImageId(null);
            projectRepository.save(project);
        } else {
            throw new RuntimeException("Project cover not found with ID: " + projectId);
        }
    }

    private void checkImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        boolean check = contentType != null && (
                contentType.equalsIgnoreCase("image/jpeg") ||
                        contentType.equalsIgnoreCase("image/png") ||
                        contentType.equalsIgnoreCase("image/gif")
        );

        if (!check) {
            throw new RuntimeException("File must be an image (JPEG, PNG, GIF)");
        }
    }

    private void checkProjectExist(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            log.error("Project with id {} does not exist", projectId);
            throw new IllegalArgumentException("Project with id = %d does not exist".formatted(projectId));
        }
    }

    private void checkFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            log.error("File is too large to upload");
            throw new IllegalArgumentException("File %s is too large to upload. Max size is %d".formatted(file.getName(), MAX_FILE_SIZE));
        }
    }
}
