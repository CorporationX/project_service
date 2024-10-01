package faang.school.projectservice.validator.resource;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;

public interface ProjectResourceValidator {

    void validateMaxStorageSize(Project project, long fileSize);

    void validateDeletePermission(TeamMember teamMember, Resource resource);
}
