package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;

public record InternshipFilterDto(InternshipStatus status, TeamRoleDto role) {
}
