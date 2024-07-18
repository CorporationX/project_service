package faang.school.projectservice.dto.project;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CreateSubProjectDto {
    private Long id;
    private String name;
    private List<Long> childrenIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long statusId;
    private Long visibilityId;
    private List<Long> stagesIds;
    private List<Long> teamsIds;
    private List<Long> momentsIds;
}
