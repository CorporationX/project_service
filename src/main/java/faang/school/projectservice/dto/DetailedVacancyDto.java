package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DetailedVacancyDto {
    private Long id;
    private String name;
    private String description;
    private TeamRole position;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private VacancyStatus status;
    private Double salary;
    private WorkSchedule workSchedule;
    private Integer count;
}
