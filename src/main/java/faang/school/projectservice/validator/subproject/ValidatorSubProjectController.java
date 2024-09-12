package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.dto.client.subproject.ProjectDto;
import org.springframework.stereotype.Component;
import software.amazon.ion.NullValueException;

@Component
public class ValidatorSubProjectController {
    public void isProjectDtoNull(ProjectDto projectDto) {
        if (projectDto == null) {
            throw new NullValueException("ProjectDto is null");
        }
    }

    public void isParentProjectNull(ProjectDto projectDto) {
        if (projectDto.getParentProjectId() == null) {
            throw new NullValueException("ParentProject is null");
        }
    }

    public void isProjectNameNull(ProjectDto projectDto) {
        if (projectDto.getName() == null) {
            throw new NullValueException("NameProject is null");
        }
    }

    public void isProjectStatusNull(ProjectDto projectDto) {
        if (projectDto.getStatus() == null) {
            throw new NullValueException("StatusProject is null");
        }
    }

    public void isProjectVisibilityNull(ProjectDto projectDto) {
        if (projectDto.getVisibility() == null) {
            throw new NullValueException("VisibilityProject is null");
        }
    }

    public void isProjectOwnerNull(ProjectDto projectDto) {
        if (projectDto.getOwnerId() == null) {
            throw new NullValueException("OwnerProject is null");
        }
    }

}
