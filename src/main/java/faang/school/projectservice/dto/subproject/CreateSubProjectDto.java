package faang.school.projectservice.dto.subproject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateSubProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Long parentProjectId;
}
