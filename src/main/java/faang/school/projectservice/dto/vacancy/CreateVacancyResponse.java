package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateVacancyResponse {
    private Long id;
    private String name;
    private String description;
    private TeamRole position;
    private Long projectId;
    private Long createdBy;
    private VacancyStatus status;
    private Double salary;
    private WorkSchedule workSchedule;
    private Integer count;
    private List<Long> requiredSkillIds;
    private String coverImageKey;
}
