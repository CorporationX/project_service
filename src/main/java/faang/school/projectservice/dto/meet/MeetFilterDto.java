package faang.school.projectservice.dto.meet;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MeetFilterDto {

    private String titlePattern;
    private LocalDateTime startDatePattern;
    private LocalDateTime endDatePattern;
}
