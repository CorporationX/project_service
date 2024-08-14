package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFilterDto {
    private Long idPattern;
    @Size(max = 255)
    private String namePattern;
    @Size(max = 255)
    private String descriptionPattern;
    private Long ownerIdPattern;
    private ProjectStatus statusPattern;
    private ProjectVisibility visibilityPattern;
}
