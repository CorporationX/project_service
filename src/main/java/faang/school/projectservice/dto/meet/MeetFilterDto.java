package faang.school.projectservice.dto.meet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetFilterDto {

    private String title;
    @DateTimeFormat(pattern = "yyyy-MM-dd['T'HH:mm:ss]")
    private LocalDateTime startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd['T'HH:mm:ss]")
    private LocalDateTime endDate;
}
