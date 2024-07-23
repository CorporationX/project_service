package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;

@Data
public class SubProjectFilterDto {
    private String name;
    private ProjectStatus status;
}
