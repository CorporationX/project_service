package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.dto.subproject.UpdateVisibilitySubprojectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.subproject.SubProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubProjectValidator {
    private final SubProjectService subProjectService;
    public void validateUpdateVisibilitySubprojectDto(UpdateVisibilitySubprojectDto updateVisibilitySubprojectDto){
        validateProjectId(updateVisibilitySubprojectDto.getId());
        validateVisibility(updateVisibilitySubprojectDto.getVisibility());
    }

    private void validateProjectId(Long id) {
        validateId(id);
        if (subProjectService.isExistProjectById(id)) {
            throw new DataValidationException("Can't be exist two project with equals id");
        }
    }

    private void validateVisibility(ProjectVisibility visibility) {
        if (visibility == null) {
            throw new DataValidationException("Visibility can't be null");
        }
    }

    private void validateId(Long id) {
        if (id == null || id < 0) {
            throw new DataValidationException("It's wrong id, id can't be null");
        }
    }
}
