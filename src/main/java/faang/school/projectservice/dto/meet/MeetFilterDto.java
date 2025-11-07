package faang.school.projectservice.dto.meet;

import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@FieldNameConstants
@Builder
public record MeetFilterDto(
        String title,
        LocalDateTime startsAt
) {
    @AssertTrue(message = "At least one filter criteria must be provided")
    public boolean isAtLeastOneFilterPresent() {
        return title != null || startsAt != null;
    }
}