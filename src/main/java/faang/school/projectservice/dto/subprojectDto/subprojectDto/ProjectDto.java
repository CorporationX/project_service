package faang.school.projectservice.dto.subprojectDto.subprojectDto;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    private Long id;
    @NotNull(message = "Project name is required")
    private String name;
    private String description;
    @NotNull(message = "Project status is required")
    private ProjectStatus status = ProjectStatus.CREATED;
    @NotNull(message = "Project visibility is required")
    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    private List<Project> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
