package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacancyDto {
    private Long id;
    @NotBlank(message = "Vacancy must have a name")
    private String name;
    @NotBlank(message = "Vacancy must have a description")
    private String description;
    @NotBlank(message = "Vacancy must be assigned to a project")
    private Long projectId;
    private List<Long> candidatesIds;
    @NotBlank(message = "Vacancy must have a curator")
    private Long curatorId;
    private VacancyStatus status;
    private Integer workersRequired;
}
