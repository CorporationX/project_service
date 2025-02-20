package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import lombok.Data;

@Data
public class VacancyFilterDto {
    TeamRole positionPattern;
    String namePattern;
}
