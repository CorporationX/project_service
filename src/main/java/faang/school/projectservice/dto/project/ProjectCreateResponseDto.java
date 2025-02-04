package faang.school.projectservice.dto.project;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectCreateResponseDto {
    private String name;
    private String description;
    private Long ownerId;
    private LocalDateTime createdAt;
}
