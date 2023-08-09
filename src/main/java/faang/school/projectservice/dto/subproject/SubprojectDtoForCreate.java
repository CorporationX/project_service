package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
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
public class SubprojectDtoForCreate {
    private Long subprojectId;

    @NotBlank(message = "name cannot be null or empty")
    private String name;

    @NotBlank(message = "description cannot be null or empty")
    private String description;

    @NotNull(message = "ownerId cannot be null")
    private Long ownerId;

    private Long parentProjectId;

    private List<Long> childrenIds;

    @Builder.Default
    private ProjectStatus status = ProjectStatus.CREATED;

    @Builder.Default
    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;

    private List<Long> stagesIds;
}
