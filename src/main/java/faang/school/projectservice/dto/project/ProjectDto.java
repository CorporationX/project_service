package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProjectDto {
    private Long id;
    @NotNull(message = "Project name must not be null")
    @NotBlank(message = "Project name must not be blank")
    @Size(max = 255, message = "Project name should not exceed 255 characters")
    private String name;
    @NotNull(message = "Project description must not be null")
    @NotBlank(message = "Project description must not be blank")
    @Size(max = 255, message = "Project description should not exceed 255 characters")
    private String description;
    private List<@NotNull Long> childrenIds;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    @NotNull(message = "Project visibility must not be null")
    private ProjectVisibility visibility;
}
