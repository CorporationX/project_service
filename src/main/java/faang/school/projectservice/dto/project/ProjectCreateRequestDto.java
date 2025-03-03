package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProjectCreateRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @Positive
    private Long ownerId;
    @NotBlank
    private ProjectVisibility visibility;
}
