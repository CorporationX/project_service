package faang.school.projectservice.dto.meet;

import java.time.LocalDateTime;

public record MeetFilterDto(
        String title,
        LocalDateTime startDate
) {
}
