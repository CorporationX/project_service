package faang.school.projectservice.dto.subprojectdto;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubProjectFilterDto {
    private String name;
    private ProjectStatus status;
}
