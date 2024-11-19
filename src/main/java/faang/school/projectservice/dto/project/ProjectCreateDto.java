package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ProjectCreateDto {
    @Positive
    private Long ownerId;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}
