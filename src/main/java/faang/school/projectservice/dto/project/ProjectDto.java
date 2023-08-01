package faang.school.projectservice.dto.project;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String status;
    private String visibility;
}
