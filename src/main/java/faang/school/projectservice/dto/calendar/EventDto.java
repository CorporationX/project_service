package faang.school.projectservice.dto.calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    private List<Integer> participantsIds;
    private Long authorId;
    private String summary;
    private String location;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private TimeZone timeZone;
}
