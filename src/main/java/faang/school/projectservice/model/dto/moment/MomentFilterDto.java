package faang.school.projectservice.model.dto.moment;

import lombok.Builder;

import java.util.List;

@Builder
public record MomentFilterDto(
        long month,
        long year,
        List<Long> projectId
) {
}
