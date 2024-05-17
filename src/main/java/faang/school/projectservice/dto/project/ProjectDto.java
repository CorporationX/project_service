package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectDto {
    private Long id;

    @NotBlank(message = "Project name can't be blank")
    private String name;

    @NotBlank(message = "Project description can't be blank")
    private String description;

    @NotNull(message = "Project owner id")
    private Long ownerId;

    private ProjectStatus status;
}