package faang.school.projectservice.dto.project;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectDto {
    private long id;
    private String name;
    private String description;
}
