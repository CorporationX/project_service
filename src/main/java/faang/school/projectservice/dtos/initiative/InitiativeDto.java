package faang.school.projectservice.dtos.initiative;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiativeDto {
    private Long id;
    private String name;
    private String description;
    private Long curatorId;
    private String status;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
