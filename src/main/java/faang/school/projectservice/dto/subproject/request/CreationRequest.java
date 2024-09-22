package faang.school.projectservice.dto.subproject.request;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreationRequest {

    @NotBlank(message = "Project name can't be null or empty")
    private String name;

    @NotBlank(message = "Project name can't be null or empty")
    private String description;

    @NotNull(message = "OwnerId can't be null")
    @Positive(message = "OwnerId must be greater than 0")
    private Long ownerId;

    @NotNull(message = "parentProjectId can't be null")
    @Positive(message = "parentProjectId must be greater than 0")
    private Long parentProjectId;

    @NotNull(message = "Visibility can't be null")
    private ProjectVisibility visibility;
}
