package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record SubProjectFilterDto(
        @Pattern(regexp = "^\\S.*$", message = "NamePattern must not be empty, but can be null")
        String namePattern,
        ProjectStatus status) {
}