package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.WorkSchedule;
import lombok.Data;

import java.util.List;

@Data
public class CreateVacancyRequest {
    private String name;
    private String description;
    private TeamRole position;
    private Long projectId;
    private Double salary;
    private WorkSchedule workSchedule;
    private Integer count;
    private List<Long> requiredSkillIds;
}
