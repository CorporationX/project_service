package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.AccessException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class ProjectGalleryValidator {
    private final int maxGallerySize;
    private final int maxImageSizeMb;
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectGalleryValidator(@Value("${project.gallery.max_gallery_size}") int maxGallerySize,
                                   @Value("${project.gallery.max_image_size_mb}") int maxImageSizeMb,
                                   ProjectRepository projectRepository) {
        this.maxGallerySize = maxGallerySize;
        this.maxImageSizeMb = maxImageSizeMb;
        this.projectRepository = projectRepository;
    }

    public void validateAddingImage(Project project, Long creatorId, MultipartFile file) {
        isUserMemberOfProject(project, creatorId);

        List<String> galleryFileKeys = project.getGalleryFileKeys();

        if (galleryFileKeys != null && galleryFileKeys.size() >= maxGallerySize) {
            throw new DataValidationException("Gallery is full. Delete some images first.");
        }

        if (file.getSize() > maxImageSizeMb * 1024L * 1024L) {
            throw new DataValidationException("Max image size is " + maxImageSizeMb + " mb.");
        }

        if (!(file.getContentType() != null && file.getContentType().startsWith("image/"))) {
            throw new DataValidationException("File content type is not supported.");
        }
    }

    public void validateDeletingImage(Project project, Long userId) {
        isUserMemberOfProject(project, userId);
    }

    public void validateGettingGallery(Project project, Long userId) {
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            if (!projectRepository.isUserMemberOfProject(project.getId(), userId)) {
                throw new AccessException("You do not have permission to access this project gallery.");
            }
        }
    }

    private void isUserMemberOfProject(Project project, Long userId) {
        if (!projectRepository.isUserMemberOfProject(project.getId(), userId)) {
            throw new AccessException("User " + userId + " is not a member of this project");
        }
    }
}
