package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    @NotNull(message = "Field id cannot be empty")
    @Positive(message = "id must be greater than zero")
    private Long id;
    @NotBlank(message = "Project must have a name")
    @Size(max = 255, message = "Name should not exceed 255 characters")
    private String name;
    @Size(max = 255, message = "Description should not exceed 255 characters")
    private String description;
    @NotNull(message = "Owner must have a curator")
    @Positive(message = "id must be greater than zero")
    private Long ownerId;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}
