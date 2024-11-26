package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
public class VacancyUpdateDto {
    @NotNull(message = "Vacancy id cannot be empty")
    @Positive(message = "Vacancy id must be a positive integer")
    private Long id;

    @NotNull(message = "Updated by id cannot be empty")
    @Positive(message = "Updater id must be a positive integer")
    private Long updatedById;

    @NotNull(message = "Vacancy status cannot be empty")
    private VacancyStatus status;

    @NotBlank(message = "Description should not be blank")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
}
