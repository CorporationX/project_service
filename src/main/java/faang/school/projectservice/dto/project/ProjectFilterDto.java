package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;

public record ProjectFilterDto(String namePattern, ProjectStatus projectStatus) {}
