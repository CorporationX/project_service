package faang.school.projectservice.dto.moment;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MomentDto {
    private int id;
    private String name;
    private LocalDateTime date;
    private List<Long> projectIds;
    private List<Long> userIds;
    private LocalDateTime createdAt;
    private LocalDateTime update;
}
