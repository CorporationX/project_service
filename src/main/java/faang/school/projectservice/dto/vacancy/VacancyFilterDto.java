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
@NoArgsConstructor
@AllArgsConstructor
public class VacancyFilterDto {
    @NotBlank(message = "Invalid name")
    private String name;

    @NotBlank(message = "Invalid description pattern")
    private String descriptionPattern;

    @NotNull(message = "Invalid vacancy status")
    private VacancyStatus vacancyStatus;

    @NotNull(message = "Invalid required skill id")
    private Long requiredSkillId;
}
