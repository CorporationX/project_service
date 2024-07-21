package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubProjectDto implements ProjectValidator{
    private Long id;
    private Long parentProjectId;
    private String name;
    private List<Long> childrenIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Builder.Default
    private ProjectStatus status = ProjectStatus.CREATED;
    @Builder.Default
    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;
    private List<Long> stagesIds;
    private List<Long> teamsIds;
    private List<Long> momentsIds;
}
