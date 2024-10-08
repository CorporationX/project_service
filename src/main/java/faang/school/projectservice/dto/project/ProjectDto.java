package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectDto {
    private Long id;

    @NotBlank(message = "Description should not be blank")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotBlank(message = "Project name should not be blank")
    @Size(max = 255, message = "Project name cannot exceed 255 characters")
    private String name;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @Size(max = 255, message = "Cover image ID cannot exceed 255 characters")
    private String coverImageId;
}