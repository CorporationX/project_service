package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class VacancyDto {

    @Positive
    private Long id;

    @NotBlank(message = "Name should not be blank")
    private String name;

    @NotBlank(message = "description should not be blank")
    @Size(min = 1, max = 255, message = "Description must be between 1 and 255 characters")
    private String description;

    @Positive
    private Long projectId;

    @NotNull
    private VacancyStatus status;

    @NotNull
    private Double salary;

    @NotNull
    private WorkSchedule workSchedule;

    private List<Long> candidateIds;
    private Integer count;
    private List<Long> requiredSkillIds;
    private Long createdBy;
    private Long updatedBy;
}
