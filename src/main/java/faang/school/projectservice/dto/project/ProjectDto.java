package faang.school.projectservice.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProjectDto {

    private Long id;
    private String name;
    private String description;
    private String status;
    private Long ownerId;
    private Instant createdAt;
    private Instant lastModified;
    private Instant updatedAt;
    
}