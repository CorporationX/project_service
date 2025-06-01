package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VacancyFilterDto {
    private String name;
    private TeamRole position;
}
