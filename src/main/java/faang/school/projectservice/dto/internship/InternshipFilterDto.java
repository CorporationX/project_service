package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@Builder
public record InternshipFilterDto(
        TeamRole role,
        InternshipStatus status
) {
    @AssertTrue(message = "At least one filter criteria must be provided")
    public boolean isAtLeastOneFilterPresent() {
        return role != null || status != null;
    }
}