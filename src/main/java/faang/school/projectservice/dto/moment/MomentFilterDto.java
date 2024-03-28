package faang.school.projectservice.dto.moment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MomentFilterDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Long> projectIds;
}
