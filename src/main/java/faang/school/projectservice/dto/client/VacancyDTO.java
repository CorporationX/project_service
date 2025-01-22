package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VacancyDTO {
    private Long id;

    @NotNull(message = "name must be filled")
    private String name;

    @NotNull(message = "description must be filled")
    private String description;

    @NotNull(message = "position must be filled")
    private TeamRole position;

    @NotNull(message = "project must be filled")
    private Long projectId;

    private List<Long> candidateIds;

    private VacancyStatus status;

    private Double salary;

    private WorkSchedule workSchedule;

    @Positive
    private Integer count;

    private List<Long> requiredSkillIds;

    private Long createdBy;
    private Long updatedBy;
}
