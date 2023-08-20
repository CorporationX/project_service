package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.project.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectByFilterDto {
    private String name;
    private ProjectStatus status;
}
