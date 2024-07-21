package faang.school.projectservice.validation.project;

import faang.school.projectservice.dto.project.ProjectDto;

public interface ProjectValidator {
    void validateProjectByOwnerIdAndNameOfProject(ProjectDto projectDto);

    void validateProjectExistence(long id);

    void validateProjectIdAndCurrentUserId(long projectId, long currentUserId);
}
