package faang.school.projectservice.dto.vacancy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VacancyDto {

    private Long id;
    @NotBlank(message = "Name must not be blank")
    private String name;
    @NotBlank(message = "Description must not be blank")
    private String description;
    @NotNull(message = "Project ID must not be null")
    private Long projectId;
    private List<Long> candidatesIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    @NotBlank(message = "Status must not be blank")
    private String status;
    @PositiveOrZero(message = "Salary must be zero or positive")
    private Double salary;
    @NotBlank(message = "Work schedule must not be blank")
    private String workSchedule;
    @NotEmpty(message = "The list of required skill IDs must not be empty")
    private List<Long> requiredSkillIds;
}
