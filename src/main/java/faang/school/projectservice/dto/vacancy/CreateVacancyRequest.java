package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.WorkSchedule;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateVacancyRequest {
    private String name;
    private String description;
    private TeamRole position;
    private Long projectId;
    private Long userCreatedBy;
    private Double salary;
    private WorkSchedule workSchedule;
    private Integer count;
    private List<Long> requiredSkillIds;
    private String coverImageKey;
}
