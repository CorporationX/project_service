package faang.school.projectservice.validation.resource;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;

public interface ProjectResourceValidator {

    void validateMaxStorageSize(Project project, long newFileLength);

    void validateDeletePermission(TeamMember teamMember, Resource resource);
}
