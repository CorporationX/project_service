package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateVisibilitySubprojectDto {
    private Long id;
    private ProjectVisibility visibility;
}
