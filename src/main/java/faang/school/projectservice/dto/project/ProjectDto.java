package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    @NotNull(message = "Поле id не может быть пустым.")
    @Positive(message = "поле id должно быть положительным и больше 0")
    private Long id;
    @NotBlank(message = "Поле name не может быть пустым.")
    private String name;
    @Size(max = 255, message = "Описание не должно превышать 255 символов.")
    private String description;
    @Positive(message = "parentProjectId должен быть положительным числом")
    private Long parentProjectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private List<Long> children;

}
