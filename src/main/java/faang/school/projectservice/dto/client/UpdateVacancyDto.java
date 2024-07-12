package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;


@Data
public class UpdateVacancyDto {
    @NotBlank(message = "Title cannot be empty")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Vacancy must have a project")
    private Long projectId;

    @NotNull
    private VacancyStatus status;

    private Double salary;

    private WorkSchedule workSchedule;

    private Integer count;

    private List<Long> requiredSkillIds;
}
