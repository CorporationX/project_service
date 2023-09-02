package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubProjectDto {
    private Long id;
    @NotBlank
    @Size(min = 1, max = 64)
    private String name;
    private String description;
    @NotNull
    private Long ownerId;
    @NotNull
    private Long parentProjectId;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}
