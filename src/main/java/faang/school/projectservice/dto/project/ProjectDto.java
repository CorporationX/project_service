package faang.school.projectservice.dto.project;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProjectDto {
    private Long id;
    private String name;
    private ProjectStatus projectStatus;
    private List<StageDto> stageList;
}
