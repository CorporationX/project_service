package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.project.ProjectStatus;
import faang.school.projectservice.model.project.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SubprojectUpdateDto {
    @NotNull
    private Long subprojectId;

    private ProjectStatus status;

    private ProjectVisibility visibility;
}
