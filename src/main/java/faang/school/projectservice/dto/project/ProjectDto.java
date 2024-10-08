package faang.school.projectservice.dto.project;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectDto {
    private Long id;
    private String description;
    private String name;
    private Long ownerId;
    private String coverImageId;
}