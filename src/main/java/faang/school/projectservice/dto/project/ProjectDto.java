package faang.school.projectservice.dto.project;


import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private List<Long> childrenId;
    private List<StageDto> stages;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}