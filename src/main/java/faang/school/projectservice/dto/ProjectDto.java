package faang.school.projectservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectDto {
    private String name;
    private String description;
}
