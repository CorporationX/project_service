package faang.school.projectservice.dto.project;

import faang.school.projectservice.controller.Marker;
import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {

    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    @NotNull(groups = Marker.getProject.class)
    private Long id;

    @NotBlank(message = "Field name cannot be empty or null")
    private String name;

    @NotBlank(message = "Field description cannot be empty or null")
    private String description;

    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private ProjectStatus status;
}
