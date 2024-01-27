package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import lombok.Data;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */
@Data
public class VacancyDto {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private List<Long> candidatesIds;
    private Double salary;
    private WorkSchedule workSchedule;
    private List<Long> requiredSkillIds;
    private VacancyStatus status;
}
