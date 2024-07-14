package faang.school.projectservice.dto.moment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MomentFilterDto {
    private Long id;
    private String name;
    private LocalDateTime date;
    private List<Long> resourcesId;
    private List<Long> projectsId;
    private List<Long> userIds;
    private String imageId;
}
