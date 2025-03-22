package faang.school.projectservice.dto.vacancy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacancyCreateDto {
    @NotBlank
    private String name;

    @NotNull
    private String position;

    @NotNull
    private Long projectId;

    @NotNull
    private Integer count;

    @NotBlank
    private String description;

    @NotNull
    private String status;

    private List<Long> requiredSkillIds;
}