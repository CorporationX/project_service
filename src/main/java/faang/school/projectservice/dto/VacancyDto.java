package faang.school.projectservice.dto;

import faang.school.projectservice.model.VacancyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Vacancy entity")
public class VacancyDto {

    private Long id;

    @NotBlank(message = "Vacancy name can't be empty")
    @Size(max = 255, message = "The name length should not exceed 255 characters")
    private String name;

    @NotBlank(message = "Vacancy description can't be empty")
    private String description;

    @NotNull(message = "Project Id in vacancy must be set and can't be 0")
    @Min(value = 1, message = "Project Id in vacancy must be set and can't be 0")
    private Long projectId;

    private List<Long> candidateIds;
    private Long createdBy;

    @NotNull(message = "Vacancy status can't be null")
    @Size(max = 50, message = "Status cannot be more than 50 characters")
    private VacancyStatus status;

    private Double salary;
    private Integer count;
    private List<Long> requiredSkillIds;
}
