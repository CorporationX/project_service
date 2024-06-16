package faang.school.projectservice.validator.project;

import faang.school.projectservice.model.Project;

public interface ProjectValidator {
    void validateProjectExistence(long id);
}
