package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateStatusSubprojectDto {
    private Long id;
    private ProjectStatus status;
}
