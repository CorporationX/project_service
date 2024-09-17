package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import lombok.*;

import java.util.Objects;
@Builder
public record ProjectFilterDto(
        String name,
        ProjectStatus projectStatus) {
}
