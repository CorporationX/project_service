package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;

public record FilterInternshipDto(
        InternshipStatus status,
        TeamMember role,

) {
}