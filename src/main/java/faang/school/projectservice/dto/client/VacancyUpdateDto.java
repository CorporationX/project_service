package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VacancyUpdateDto {

    @NotBlank(message =  "Name cannot be empty")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    @Size(message = "Description must contain no more than 200 characters")
    private String description;

    @NotNull(message = "Salary cannot be null")
    private Double salary;

    @NotBlank(message = "Schedule cannot be empty")
    private WorkSchedule workSchedule;

    @NotNull(message = "Status cannot be null")
    private VacancyStatus status;

    @NotNull(message = "Skills cannot be null")
    private List<Long> requiredSkillIds;
}
