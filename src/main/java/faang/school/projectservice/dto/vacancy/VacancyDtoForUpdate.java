package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacancyDtoForUpdate {
    @NotNull(message = "VacancyId cannot be null")
    private Long vacancyId;

    @NotBlank(message = "name cannot be null or empty")
    private String name;

    @NotBlank(message = "description cannot be null or empty")
    private String description;

    @NotNull(message = "updateBy cannot be null")
    private Long updatedBy;

    @NotBlank(message = "vacancyStatus cannot be null or empty")
    private VacancyStatus status;
}
