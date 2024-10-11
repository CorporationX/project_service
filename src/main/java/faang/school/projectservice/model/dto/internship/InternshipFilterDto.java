package faang.school.projectservice.model.dto.internship;

import faang.school.projectservice.model.entity.InternshipStatus;
import faang.school.projectservice.model.entity.TeamRole;
import lombok.Builder;

@Builder
public record InternshipFilterDto(
        InternshipStatus internshipStatus,
        TeamRole teamRole) {
}
