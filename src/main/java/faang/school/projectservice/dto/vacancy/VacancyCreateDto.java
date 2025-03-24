package faang.school.projectservice.dto.vacancy;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
public class VacancyCreateDto  {
    protected Long id;
    @NotNull (message = "Name must not be null") private String name;
    @NotNull(message = "Project Id must not be null")
    @Positive(message = "Project Id must be positive number") private Long projectId;
    protected String description;
    @NotNull(message = "Position Id must not be null")
    protected Integer positionId;
    protected Double salary;
    protected String coverImageKey;
    protected List<Long> requiredSkillIds;
    protected Integer count;
    @NotNull(message = "Candidates Ids must not be null")
    protected List<Long> candidatesIds;
    @NotNull(message = "Status Id must not be null")
    protected Integer statusId;
    protected Long roleId;
}

