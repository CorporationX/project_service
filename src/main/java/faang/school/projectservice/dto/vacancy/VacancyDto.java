package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class VacancyDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @Min(1L)
    @NotNull
    private Long projectId;

    @NotNull
    List<@NotNull @Min(1) Long> candidateIds;

    private LocalDateTime createdAt;

    @Min(0)
    @NotNull
    private Double salary;

    @NotNull
    WorkSchedule workSchedule;

    @NotNull
    List<@NotNull @Min(1) Long> requiredSkillIds;

    @Min(1)
    @NotNull
    int count;
}