package faang.school.projectservice.validator;

import faang.school.projectservice.model.dto.ProjectDto;
import org.springframework.stereotype.Component;
import software.amazon.ion.NullValueException;

@Component
public class ValidatorSubProjectController {
    public void validateProjectDtoNotNull(ProjectDto projectDto) {
        if (projectDto == null) {
            throw new NullValueException("ProjectDto is null");
        }
    }

    public void validateParentProjectNull(ProjectDto projectDto) {
        if (projectDto.getParentProjectId() == null) {
            throw new NullValueException("ParentProject is null");
        }
    }

    public void validateProjectNameNotNull(ProjectDto projectDto) {
        if (projectDto.getName() == null) {
            throw new NullValueException("NameProject is null");
        }
    }

    public void validateProjectStatusNotNull(ProjectDto projectDto) {
        if (projectDto.getStatus() == null) {
            throw new NullValueException("StatusProject is null");
        }
    }

    public void validateProjectVisibilityNotNull(ProjectDto projectDto) {
        if (projectDto.getVisibility() == null) {
            throw new NullValueException("VisibilityProject is null");
        }
    }

    public void validateProjectOwnerNull(ProjectDto projectDto) {
        if (projectDto.getOwnerId() == null) {
            throw new NullValueException("OwnerProject is null");
        }
    }
}
