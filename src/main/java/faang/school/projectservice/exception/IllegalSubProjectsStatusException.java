package faang.school.projectservice.exception;

import faang.school.projectservice.model.ProjectStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IllegalSubProjectsStatusException extends IllegalStateException {
    public IllegalSubProjectsStatusException(long parentProjectId, ProjectStatus projectStatus) {
        super(String.format("To declare project with ID: %d as %s, all subprojects should have the same status",
                parentProjectId, projectStatus));
        log.error(super.getMessage());
    }
}
