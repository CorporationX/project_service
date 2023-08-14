package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Long ownerId;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}
