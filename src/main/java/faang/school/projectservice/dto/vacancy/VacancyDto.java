package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyDto {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 150)
    private String description;

    @PositiveOrZero
    private Long projectId;

    @PositiveOrZero
    private Long createdBy;

    @PositiveOrZero
    private Long updatedBy;

    @NotNull
    private VacancyStatus status;

    @PositiveOrZero
    private Double salary;

    private WorkSchedule workSchedule;

    @PositiveOrZero
    private int count;

    private List<Long> candidateIds;
}
