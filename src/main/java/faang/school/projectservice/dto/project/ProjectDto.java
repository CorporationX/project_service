package faang.school.projectservice.dto.project;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProjectDto {
    private Long id;
    private Long parentProjectId;
    private String name;
    private List<Long> childrenIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Builder.Default
    @JsonSetter(nulls = Nulls.SKIP)
    private ProjectStatus status = ProjectStatus.CREATED;
    @Builder.Default
    @JsonSetter(nulls = Nulls.SKIP)
    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;
    private List<Long> stagesIds;
    private List<Long> teamsIds;
}
