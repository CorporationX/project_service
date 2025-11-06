package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;

import java.util.List;

public record UpdateInternshipDto(
        List<Long> aheadOfScheduleTeamMembersId,
        List<Long> dismissedTeamMembersId,
        InternshipStatus status,
        TeamRole role
) {
}
