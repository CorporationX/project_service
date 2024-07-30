package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSubProjectDto {
    @NotBlank(message = "Project name can't be null or empty")
    @Size(max = 128, message = "Project name must not exceed 128 characters")
    private String name;
    @NotBlank(message = "Project description can't be null or empty")
    @Size(max = 4000, message = "Project description must not exceed 4000 characters")
    private String description;
    private Long ownerId;
    @NotNull(message = "Parent Project ID can't be null")
    private Long parentProjectId;
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;
    @Enumerated(EnumType.STRING)
    private ProjectVisibility visibility;
}
