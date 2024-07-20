package faang.school.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
}
