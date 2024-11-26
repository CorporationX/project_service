package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Validated
public class VacancyResponseDto {

    private long id;

    @NotBlank(message = "Name should not be blank")
    @Size(max = 255, message = "Name must be less than or equal to 255 characters")
    private String name;

    @NotBlank(message = "Description should not be blank")
    @Size(max = 255, message = "Description must be less than or equal to 255 characters")
    private String description;

    private long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;
    private Long updatedById;

    private VacancyStatus status;

    private Double salary;

    private WorkSchedule workSchedule;

    private Integer count;

    private List<Long> requiredSkillIds;
}
