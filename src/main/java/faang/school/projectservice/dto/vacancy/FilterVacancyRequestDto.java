package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record FilterVacancyRequestDto(
        @Nullable
        TeamRole position,
        @Nullable
        String namePattern) {
}
