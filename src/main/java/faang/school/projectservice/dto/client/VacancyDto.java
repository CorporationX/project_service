package faang.school.projectservice.dto.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VacancyDto {
    private Long id;
    @NotBlank(message = "Title cannot be empty")
    private String name;
    @NotBlank
    private String description;

    @NotNull(message = "Vacancy must have a project")
    private Long projectId;

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
}
