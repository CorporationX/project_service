package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import lombok.Data;

/**
 * @author Alexander Bulgakov
 */

@Data
public class VacancyFilterDto {
    private String name;
    private TeamRole position;
}
