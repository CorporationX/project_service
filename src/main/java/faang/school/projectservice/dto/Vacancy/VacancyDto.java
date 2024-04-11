package faang.school.projectservice.dto.Vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import lombok.Data;

@Data public class VacancyDto {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private VacancyStatus vacancyStatus;
    private Double salary;
    private Integer count;
    private TeamRole position;
}
