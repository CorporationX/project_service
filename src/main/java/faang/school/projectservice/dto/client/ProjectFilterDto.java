package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectFilterDto {
    private ProjectStatus status;
    private String name;
}
