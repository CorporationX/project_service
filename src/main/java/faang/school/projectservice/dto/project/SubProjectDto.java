package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SubProjectDto {
    private Long id;
    private String name;
    private String description;
    private long ownerId;
    private Long parentProjectId;
    private List<Long> childrenIds;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private List<Long> stagesId;
}
