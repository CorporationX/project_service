package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectFilterDto {
    private String namePattern;
    private ProjectStatus statusPattern;
}
