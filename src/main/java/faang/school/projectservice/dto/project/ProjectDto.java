package faang.school.projectservice.dto.project;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProjectDto {

    private Long id;
    private String name;
    private String description;
    private String status;
    private Long ownerId;
    private boolean isPrivate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}