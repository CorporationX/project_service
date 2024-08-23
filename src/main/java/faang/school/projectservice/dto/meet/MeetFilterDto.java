package faang.school.projectservice.dto.meet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetFilterDto {
    private String titlePattern;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
