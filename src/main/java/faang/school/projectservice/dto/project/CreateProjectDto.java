package faang.school.projectservice.dto.project;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateProjectDto {
    private Long id;
    private String description;
    private Long ownerId;
    private Long parentProjectId;
    private LocalDateTime createdAt;
}
