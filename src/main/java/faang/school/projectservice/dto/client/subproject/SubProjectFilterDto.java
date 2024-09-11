package faang.school.projectservice.dto.client.subproject;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;

@Data
public class SubProjectFilterDto {
    private ProjectStatus status;
    private String name;
}
