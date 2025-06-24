package faang.school.projectservice.validation.gallery;

import faang.school.projectservice.exception.common.DataValidationException;
import faang.school.projectservice.exception.common.PreConditionFailedException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class GalleryValidation {

    private final GalleryProperties galleryProperties;
    private final ProjectRepository projectRepository;

    public void validateGalleryLimit(long projectId, int addedFiles) {
        int gallerySize = projectRepository.countFilesByIdProjectId(projectId);
        if (gallerySize + addedFiles > galleryProperties.getMaxLimit()) {
            throw new PreConditionFailedException("Limit of %d items exceeded!".formatted(galleryProperties.getMaxLimit()));
        }
    }

    public void validateAllowedContentTypes(List<MultipartFile> files) {
        List<String> notAllowedTypes = files.stream()
                .filter(file -> Objects.nonNull(file.getContentType())
                        && !galleryProperties.getAllowedContentTypes().contains(file.getContentType()))
                .map(MultipartFile::getContentType)
                .toList();
        if (!notAllowedTypes.isEmpty()) {
            throw new DataValidationException("Gallery not allowed types %s".formatted(notAllowedTypes));
        }
    }

    public void validateProjectIsPublic(Project project) {
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            throw new PreConditionFailedException("Project is private!");
        }
    }

    public void validateFileIsProjectGalleryItem(Project project, Resource resource) {
        if(!projectRepository.findFileKeysByProjectId(project.getId()).contains(resource.getKey())) {
            throw new PreConditionFailedException("Not gallery file!");
        }
    }
}