package faang.school.projectservice.vacancy.dto;

import faang.school.projectservice.model.TeamRole;
import lombok.Data;

@Data
public class VacancyUpdateDto {
    private String title;
    private TeamRole position;
    private int slots;
}
