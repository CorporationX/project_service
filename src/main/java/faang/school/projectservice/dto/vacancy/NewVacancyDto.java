package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.WorkSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Builder
@Validated
public class NewVacancyDto {

    @NotBlank(message = "Vacancy title cannot be empty")
    @Size(max = 255, message = "Vacancy title must be under 255 characters long")
    private String name;

    @NotBlank(message = "Vacancy description cannot be empty")
    @Size(min = 10, max = 600, message = "Vacancy description must be at least 10 and under 600 characters long")
    private String description;

    @NotNull(message = "Project id cannot be empty")
    @Positive(message = "Project id must be a positive integer")
    private Long projectId;

    @NotNull(message = "Created by id cannot be empty")
    @Positive(message = "Creator id must be a positive integer")
    private Long createdById;

    @NotNull(message = "Slavery is prohibited")
    @Positive(message = "Salary must be a positive number")
    private Double salary;

    @NotNull(message = "Work schedule cannot be empty")
    private WorkSchedule workSchedule;

    @Schema(description = "Number of employees needed")
    @NotNull(message = "Number of vacancies cannot be empty")
    @Positive(message = "Number of vacancies must be a positive integer")
    private Integer count;

    @NotEmpty(message = "Required skills ids list cannot be null or empty")
    private List<Long> requiredSkillIds;

    @Size(max = 255, message = "Pattern must be under 255 characters long")
    private String requestFilterPattern;
}
