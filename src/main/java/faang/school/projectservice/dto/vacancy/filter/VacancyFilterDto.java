package faang.school.projectservice.dto.vacancy.filter;

import faang.school.projectservice.model.TeamRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VacancyFilterDto {
    private String namePattern;
    private TeamRole teamRole;
}
