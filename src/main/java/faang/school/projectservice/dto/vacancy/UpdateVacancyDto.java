package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateVacancyDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private List<Long> candidateIds;
    private VacancyStatus status;
    @PositiveOrZero
    private Double salary;
    private WorkSchedule workSchedule;
    private Integer count;
    @NotEmpty
    private List<Long> requiredSkillIds;

}
