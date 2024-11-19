package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubProjectDto {

    @Schema(description = "Unique identifier of the project", example = "1")
    @NotNull
    @Positive
    private Long id;

    @Schema(description = "Name of the project", example = "Project name")
    @NotBlank(message = "Project name cannot be blank and not null")
    private String name;

    @Schema(description = "List of child project ids", example = "[1, 2, 3]", nullable = true)
    private List<@Positive Long> childrenIds;

    @Builder.Default
    @Schema(description = "Status of the project", example = "CREATED", defaultValue = "CREATED")
    private ProjectStatus status = ProjectStatus.CREATED;

    @Builder.Default
    @Schema(description = "Visibility of the project", example = "PUBLIC", defaultValue = "PUBLIC")
    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;

    @Schema(description = "Date and time creating the project", example = "2022-01-01 00:00:00Z", nullable = true)
    private LocalDateTime createdAt;

    @Schema(description = "Date and time updating the project", example = "2022-01-01 00:00:00Z", nullable = true)
    private LocalDateTime updatedAt;

    @Schema(description = "List of stages ids", example = "[1, 2, 3]", nullable = true)
    private List<@Positive Long> stagesIds;

    @Schema(description = "List of teams ids", example = "[1, 2, 3]", nullable = true)
    private List<@Positive Long> teamsIds;
}
