package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectStatusFilterDto {
    private ProjectStatus status;
}
