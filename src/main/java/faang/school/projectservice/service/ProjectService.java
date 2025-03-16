package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;

import java.util.Optional;

public interface ProjectService {
    Optional<Project> getProjectByIdOrEmpty(long projectId);
}
