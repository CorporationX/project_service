package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateVacancyDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Long projectId;
    @NotNull
    private TeamRole position;
    @NotNull
    @Min(1)
    private Integer count;
    private String status;
}