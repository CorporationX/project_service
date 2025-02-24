package faang.school.projectservice.filter.project;

import faang.school.projectservice.model.ProjectStatus;

public record ProjectFilterDto(String name, ProjectStatus status) {
}
