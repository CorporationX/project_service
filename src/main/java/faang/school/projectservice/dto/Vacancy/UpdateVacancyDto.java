package faang.school.projectservice.dto.Vacancy;

import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateVacancyDto {
    @NotNull
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private List<Long> candidateIds;;
    private Integer count;
    private VacancyStatus status;
    private Double salary;
    private WorkSchedule workSchedule;
    private List<Long> requiredSkillIds;
}
