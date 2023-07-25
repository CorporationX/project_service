package faang.school.projectservice.dto.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String status;

    public ProjectDto(String name, String description, Long ownerId) {
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
    }

}
