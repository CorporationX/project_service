package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
public class VacancyDto {
    private long vacancyId;
    private String name;
    private String description;
    private TeamRole position;
    private Project project;
    private List<Candidate> candidates;
    private LocalDateTime updatedAt;
    private long updatedBy;
    private VacancyStatus status;
    private double salary;
    private WorkSchedule workSchedule;
    private Integer count;
    private List<Long> requiredSkills;

}
