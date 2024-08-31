package faang.school.projectservice.dto.vacancy;


import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated({CreateVacancy.class})
public class VacancyDto {

    @NotEmpty(message = "Name must not be empty", groups = CreateVacancy.class)
    @Size(max = 128, message = "Name must be less than 128 characters", groups = CreateVacancy.class)
    private String name;

    @NotEmpty(message = "Description must not be empty", groups = CreateVacancy.class)
    @Size(max = 4096, message = "Description must be less than 4096 characters", groups = CreateVacancy.class)
    private String description;

    @NotNull(message = "Project ID must not be null", groups = CreateVacancy.class)
    private Long projectId;

    @NotNull(message = "Status must not be null", groups = CreateVacancy.class)
    private VacancyStatus status;

    @NotNull(message = "Salary must not be null", groups = CreateVacancy.class)
    private Double salary;

    @NotNull(message = "Work schedule must not be null", groups = CreateVacancy.class)
    private WorkSchedule workSchedule;

    @NotNull(message = "Count must not be null", groups = CreateVacancy.class)
    private Integer count;

    @NotNull(message = "Required skill IDs must not be null", groups = CreateVacancy.class)
    private List<Long> requiredSkillIds;

    @NotNull(message = "Owner ID must not be null", groups = CreateVacancy.class)
    private Long ownerId;

    @NotNull(message = "Team role must not be null", groups = CreateVacancy.class)
    private TeamRole teamRole;
}