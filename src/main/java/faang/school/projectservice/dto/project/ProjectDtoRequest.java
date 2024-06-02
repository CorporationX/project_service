package faang.school.projectservice.dto.project;


import faang.school.projectservice.model.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDtoRequest {
    private String name;
    private String description;
    private Long ownerId;
    private ProjectVisibility visibility;
}
