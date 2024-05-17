package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDto {

    private Long id;
    @NotNull
    private String name;
    private String description;
    private Long parentId;
    private List<Long> childrenIds;
    private ProjectVisibility visibility;
    private ProjectStatus status;
}
