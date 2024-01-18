package faang.school.projectservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProjectDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String description;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long ownerId;
    @Builder.Default
    private ProjectStatus status = ProjectStatus.CREATED;
    @Builder.Default
    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;
}
