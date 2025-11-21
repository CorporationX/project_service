package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.annotation.Nullable;

public record InternshipFilterDto(
        @Nullable
        InternshipStatus status,
        @Nullable
        TeamRole role,
        @Nullable
        String name
) {
}