package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 255)
    private String description;

    @PositiveOrZero
    private Long ownerId;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}