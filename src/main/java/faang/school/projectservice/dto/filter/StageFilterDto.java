package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;

@Data
public class StageFilterDto {
    private ProjectStatus projectStatus;
    private Integer page;
    private Integer size;
}
