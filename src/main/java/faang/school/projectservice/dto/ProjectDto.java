package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private ProjectStatus status;
}
