package faang.school.projectservice.dto.vacancy;


import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @Size(max = 255, message = "Name should not exceed 255 characters")
    private String name;
    @NotBlank(message = "Vacancy must have a description")
    @Size(max = 255, message = "Description should not exceed 255 characters")
    private String description;
    @NotNull(message = "Vacancy must be assigned to a project")
    @Positive(message = "id must be greater than zero")
    private Long projectId;
    private List<Long> candidatesIds;
    @NotNull(message = "Vacancy must have a curator")
    @Positive(message = "id must be greater than zero")
    private Long curatorId;
    private VacancyStatus status;
    private Integer workersRequired;
    private Long tutorId;
}
