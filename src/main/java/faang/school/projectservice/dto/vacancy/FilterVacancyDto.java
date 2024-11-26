package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.WorkSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Filters for the vacancy search")
@Validated
public class FilterVacancyDto {

    @Schema(description = "Vacancy title")
    @NotBlank(message = "Title should not be blank")
    @Size(max = 255, message = "Title should not exceed 255 characters")
    private String title;

    @Schema(description = "Salary", minimum = "0.0")
    @Positive(message = "Salary should be a positive number")
    private Double salary;

    @Schema(description = "Work schedule")
    private WorkSchedule workSchedule;

    @Schema(description = "Pattern for filtering")
    @Size(max = 255, message = "Pattern should not exceed 255 characters")
    private String pattern;
}
