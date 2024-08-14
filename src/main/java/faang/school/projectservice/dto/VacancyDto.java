package faang.school.projectservice.dto.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VacancyDto {
    private Long id;
    @NotEmpty(message = "Title cannot be empty")
    private String name;

    @NotEmpty(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Vacancy must have a project")
    private Long projectId;

    //@Size(min = 1, message = "Candidates must be not empty")
    private List<Long> candidateIds;

    @NotNull
    private VacancyStatus status;

    private Double salary;

    private WorkSchedule workSchedule;

    private Integer count;

    private List<Long> requiredSkillIds;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @NotNull(message = "user is not set")
    private Long createdBy;

    @NotNull(message = "user is not set")
    private Long updatedBy;

}