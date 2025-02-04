package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;

@Data
public class ProjectFilterDto {
    private String namePattern;
    private ProjectStatus projectStatus;
}
