package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import lombok.Data;

import java.util.List;

@Data
public class VacancyDto {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private List<Long> candidateIds;
    private VacancyStatus status;
    private Double salary;
    private WorkSchedule workSchedule;
    private Integer count;
    private List<Long> requiredSkillIds;
    private Long created_by;
    private Long updated_by;
}
