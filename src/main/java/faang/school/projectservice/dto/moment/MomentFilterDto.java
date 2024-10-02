package faang.school.projectservice.dto.moment;

import lombok.Builder;

import java.util.List;

@Builder
public record MomentFilterDto(
        long month,
        long year,
        List<Long> projectId
) {
}
