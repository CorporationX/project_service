package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record ProjectFilterDto(
        @Nullable
        String name,

        @Nullable
        ProjectStatus status
) {
}
