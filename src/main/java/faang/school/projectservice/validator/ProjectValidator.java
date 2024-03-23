package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

public class ProjectValidator {

    public void toCreate(CreateSubProjectDto subProjectDto) {
        validateSubProjectNameWritten(subProjectDto.getName());
        validateSubProjectDescriptionWritten(subProjectDto.getDescription());
        validateSubProjectParentIdWritten(subProjectDto.getParentProjectId());
        validateSubProjectVisibilityWritten(subProjectDto.getVisibility());
    }

    public void checkProjectDto(ProjectDto projectDto) {
        validateSubProjectNameWritten(projectDto.getName());
        validateSubProjectVisibilityWritten(projectDto.getVisibility());
        validateSubProjectStatusWritten(projectDto.getStatus());
    }

    private void validateSubProjectStatusWritten(ProjectStatus status) {
        if (status == null) {
            throw new NullPointerException("Не заполнен статус для передаваемого подпроекта");
        }
    }

    private void validateSubProjectVisibilityWritten(ProjectVisibility visibility) {
        if (visibility == null) {
            throw new NullPointerException("Не заполнена видимость для передаваемого подпроекта");
        }
    }

    private void validateSubProjectParentIdWritten(Long parentProjectid) {
        if (parentProjectid == null) {
            throw new NullPointerException("Не заполнен идентификатор родительского проекта");
        }
    }

    private void validateSubProjectNameWritten(String name) {
        if (name.isEmpty()) {
            throw new NullPointerException("Не было указано название проекта");
        }
    }

    private void validateSubProjectDescriptionWritten(String description) {
        if (description.isEmpty()) {
            throw new NullPointerException("Не было указано описание проекта.");
        }
    }
}
