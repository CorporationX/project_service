package faang.school.projectservice.dto.client.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDto {
    private Long id;

    @NotBlank(message = "Project name should not be blank")
    private String name;

    @NotNull(message = "Project status must be provided")
    private ProjectStatus status;

    @NotNull(message = "Project visibility must be provided")
    private ProjectVisibility visibility;

    private Long parentProjectId;
    private Long ownerId;
    private List<Long> childrenIds;
}