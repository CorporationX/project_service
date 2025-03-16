package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class VacancyFilterRequestDto {
    @Nullable
    private TeamRole position;
    @Nullable
    private String namePattern;
}
