package faang.school.projectservice.dto.moment.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentFilterDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Long> projectIds;
}