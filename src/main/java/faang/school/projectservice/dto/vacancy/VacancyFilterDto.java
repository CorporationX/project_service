package faang.school.projectservice.dto.vacancy;

import lombok.Builder;

@Builder
public record VacancyFilterDto(
        String namePattern,
        String positionPattern
) {
}