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

import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateDto {

    @NotBlank(message = "Project name cannot be empty")
    @Size(max = 128, message = "Project name cannot exceed 128 characters")
    private String name;

    @Size(max = 4096, message = "Description cannot exceed 4096 characters")
    private String description;

    @Positive(message = "Description cannot exceed 4096 characters")
    private BigInteger maxStorageSize;

    @NotNull(message = "You must specify the project owner")
    @Positive(message = "Project owner id must be positive")
    private Long ownerId;

    @Positive(message = "Parent project id must be positive")
    private Long parentProjectId;

    private ProjectStatus status;

    @NotNull(message = "Project visibility must be specified")
    private ProjectVisibility visibility;

    @Size(max = 255, message = "Cover image id cannot exceed 255 characters")
    private String coverImageId;
}
