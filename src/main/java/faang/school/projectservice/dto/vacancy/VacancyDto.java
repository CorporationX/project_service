package faang.school.projectservice.dto.vacancy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VacancyDto {
    private Long vacancyId;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Long projectId;

    @NotNull
    private Long createdBy;

    private String status;
}
