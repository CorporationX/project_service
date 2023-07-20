package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectFilterDto {
    private String projectNamePattern;
    private ProjectStatus status;
}
