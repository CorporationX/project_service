package faang.school.projectservice.dto.client.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSubProjectDto {
    private Long id;

    @NotBlank(message = "Project name should not be blank")
    private String name;

    @NotNull(message = "Project status must be provided")
    private ProjectStatus status;

    @NotNull(message = "Project visibility must be provided")
    private ProjectVisibility visibility;

    @NotNull(message = "Parent project ID must be provided")
    private Long parentProjectId;

    @NotNull(message = "Owner ID must be provided")
    private Long ownerId;
}