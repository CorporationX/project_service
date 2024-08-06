package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VacancyDto {
    private Long id;
    @NotBlank(message = "Name cant be empty or null")
    private String name;
    @NotEmpty(message = "Description cant be empty or null")
    private String description;
    @NotNull(message = "Project cant be null")
    private Long projectId;
    private List<Long> candidateIds;
    private Long createdBy;
    private VacancyStatus status;
    private Double salary;
    private Integer count;
    private List<Long> requiredSkillIds;
}
