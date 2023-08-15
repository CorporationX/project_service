package faang.school.projectservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MomentDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime date;
    private List<Long> resourceIds;
    private List<Long> projectIds;
    private List<Long> userIds;
    private String imageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
