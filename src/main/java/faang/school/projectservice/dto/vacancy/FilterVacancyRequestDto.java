package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import org.springframework.lang.Nullable;

public record FilterVacancyRequestDto(
        @Nullable TeamRole position,
        @Nullable String namePattern) {
}
