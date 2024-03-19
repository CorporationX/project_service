package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CreateSubProjectDto {
    private String name;
    private String description;
    private Long parentProjectId;
    private ProjectVisibility visibility;
}
