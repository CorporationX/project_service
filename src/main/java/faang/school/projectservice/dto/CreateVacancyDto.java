package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateVacancyDto {
    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 3000)
    private String description;

    @NotNull
    private TeamRole position;

    @NotNull
    private Long projectId;

    @Positive
    private Double salary;

    @Enumerated(EnumType.STRING)
    private WorkSchedule workSchedule;

    @Min(1)
    private Integer count;

    private List<Long> requiredSkillIds;

    private String coverImageKey;
}
