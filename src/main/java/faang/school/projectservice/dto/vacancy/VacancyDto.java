package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.validator.vacancy.Operation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyDto {
    @NotNull(message = "Id must be not null", groups = {Operation.OnUpdate.class})
    private Long id;

    @NotNull(message = "name can't be null")
    @NotBlank(message = "name can't be blank")
    private String name;

    @NotNull(message = "description can't be null")
    @NotBlank(message = "description can't be blank")
    private String description;

    @NotNull(message = "project id can't be null", groups = {
            Operation.OnUpdate.class
    })
    private Long projectId;

    private List<Long> candidateIds;

    private LocalDateTime createdAt;

    @NotNull(message = "field creatorBy can't be null")
    private Long createdBy;

    @NotNull(message = "field updatedBy can't be null", groups = {Operation.OnUpdate.class})
    private Long updatedBy;

    @NotNull(message = "status can't be null")
    private VacancyStatus status;

    private Double salary;

    private WorkSchedule workSchedule;
}