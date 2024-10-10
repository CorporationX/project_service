package faang.school.projectservice.model.dto.vacancy;

import lombok.Builder;

@Builder
public record VacancyFilterDto(
        String namePattern,
        String positionPattern
) {
}