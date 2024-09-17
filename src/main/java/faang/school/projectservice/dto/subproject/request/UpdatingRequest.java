package faang.school.projectservice.dto.subproject.request;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatingRequest {

    @NotBlank(message = "Project name can't be null or empty")
    private String name;

    @NotBlank(message = "Project name can't be null or empty")
    private String description;

    @NotNull(message = "Status can't be null")
    private ProjectStatus status;

    @NotNull(message = "Visibility can't be null")
    private ProjectVisibility visibility;
}
