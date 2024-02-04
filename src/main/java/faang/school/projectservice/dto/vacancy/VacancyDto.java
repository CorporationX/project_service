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
    private String position;
    private Long projectId;
    private Long createdBy;
    private Long candidatesCount;
    private List<Long> candidatesIds;
    private Double salary;
    private WorkSchedule workSchedule;
    private List<Long> requiredSkillIds;
    private VacancyStatus status;
}
