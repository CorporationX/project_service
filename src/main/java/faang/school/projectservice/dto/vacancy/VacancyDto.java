package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VacancyDto {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private List<Long> candidates;
    private Long createdBy;
    private VacancyStatus status;
    private Double salary;
    private WorkSchedule workSchedule;
    private Integer count;
    private List<Long> requiredSkillIds;
}
