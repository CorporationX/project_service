package faang.school.projectservice.service.meet.filter;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MeetFilters {
    private String title;
    private LocalDateTime begin;
    private LocalDateTime end;
}
