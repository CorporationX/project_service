package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;

public record FilterSubProjectDto(String subProjectNamePattern,
                                  ProjectStatus status) {
}