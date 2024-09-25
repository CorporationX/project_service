package faang.school.projectservice.dto.moment;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MomentDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime date;
    private List<Long> projectIds;
    private List<Long> userIds;
    private String imageId;
}