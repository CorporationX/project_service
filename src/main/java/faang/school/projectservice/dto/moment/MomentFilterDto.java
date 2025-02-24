package faang.school.projectservice.dto.moment;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MomentFilterDto(
        LocalDateTime dateFrom,
        LocalDateTime dateTo,
        List<Long> projectsIds
) {
}
