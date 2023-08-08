package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class VacancyDto {
    private Long id;

    @NotBlank(message = "Invalid name")
    private String name;

    @NotNull(message = "Invalid project id")
    private Long projectId;

    @NotNull(message = "Invalid creator id")
    private Long createdBy;

    @NotNull(message = "Invalid updater id")
    private Long updatedBy;

    @NotNull(message = "Invalid vacancy status")
    private VacancyStatus status;
}
