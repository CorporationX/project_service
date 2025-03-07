package faang.school.projectservice.dto.vacancy;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
public class VacancyUpdateDto {
    @NotNull(message = "Id must not be null")
    @Positive(message = "Id must be positive number")
    protected Long id;
    @NotNull(message = "Name must not be null")
    private String name;
    @NotNull(message = "Project Id must not be null")
    @Positive(message = "Project Id must be positive number")
    private Long projectId;
    protected Double salary;
    protected String coverImageKey;
    protected List<Long> requiredSkillIds;
    @NotNull(message = "Count must not be null")
    protected Integer count;
    protected List<Long> candidatesIds;
    @NotNull(message = "Status Id must not be null")
    protected Integer statusId;
    protected Long roleId;
    protected String description;
    @NotNull(message = "Position Id must not be null")
    protected Integer positionId;
}

