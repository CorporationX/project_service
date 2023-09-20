package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    @Value("${image.cover.maxSize}")
    private long maxSize;
    private final ProjectRepository projectRepository;

    public void existProjectValidator(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException(String.format("Project not found by id: %s", projectId));
        }
    }

    public void validateSizeFile(MultipartFile multipartFile) {
        if (multipartFile == null) {
            throw new DataValidationException("File is null");
        }
        if (multipartFile.getSize() > maxSize) {
            throw new DataValidationException("File size must be less than 5 MB");
        }
    }
}