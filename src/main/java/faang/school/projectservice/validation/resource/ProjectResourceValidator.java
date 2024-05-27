package faang.school.projectservice.validation.resource;

import faang.school.projectservice.model.Project;

public interface ProjectResourceValidator {

    void validateMaxStorageSize(Project project, long newFileLength);

    void validateExistence(String key);
}
