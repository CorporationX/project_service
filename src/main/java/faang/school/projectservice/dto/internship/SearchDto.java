package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;

public record SearchDto(
        TeamRole role,
        InternshipStatus status
) {
}
