package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubProjectDto {
    @Null(message = "SubProject ID should be null for creating")
    private Long id;
    @NotBlank(message = "Project name can't be null or empty")
    @Size(max = 128, message = "Project name must not exceed 4000 characters")
    private String name;
    @NotBlank(message = "Project description can't be null or empty")
    @Size(max = 4000, message = "Project description must not exceed 4000 characters")
    private String description;
    private Long ownerId;
    @NotNull(message = "Parent Project ID can't be null")
    private Long parentProjectId;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}
