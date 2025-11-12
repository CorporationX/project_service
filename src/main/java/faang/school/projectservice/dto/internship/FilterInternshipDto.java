package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import jakarta.annotation.Nullable;

@Nullable
public record FilterInternshipDto(
        InternshipStatus status,
        TeamMember role,
        Internship id
) {
}