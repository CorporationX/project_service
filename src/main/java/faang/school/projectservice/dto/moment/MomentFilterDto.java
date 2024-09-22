package faang.school.projectservice.dto.moment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MomentFilterDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
