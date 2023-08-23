package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.project.ProjectStatus;
import faang.school.projectservice.model.project.ProjectVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneralSubprojectDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long subprojectId;

    @NotBlank(message = "name cannot be null or empty")
    private String name;

    @NotBlank(message = "description cannot be null or empty")
    private String description;

    @NotNull(message = "ownerId cannot be null")
    private Long ownerId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long parentProjectId;

    @NotNull
    private List<Long> childrenIds;

    @Builder.Default
    private ProjectStatus status = ProjectStatus.CREATED;

    @Builder.Default
    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;

    @NotNull
    private List<Long> stagesIds;
}
