package faang.school.projectservice.dto.client.moment;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MomentResponseDto {
    private long id;

    private String name;

    private String description;

    private LocalDateTime date;

    private List<Long> resourceIds;

    private List<Long> projectIds;

    private List<Long> teamMembersIds;

    private String imageId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long createdBy;

    private Long updatedBy;
}
