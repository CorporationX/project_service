package faang.school.projectservice.validator.project;

import faang.school.projectservice.model.Project;

public interface ProjectValidator {
    Project validateProjectExistence(long id);
}
