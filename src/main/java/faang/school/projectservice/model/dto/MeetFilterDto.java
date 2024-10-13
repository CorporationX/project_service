package faang.school.projectservice.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MeetFilterDto {
    private String title;
    private LocalDateTime startDateAfter;
    private LocalDateTime startDateBefore;
    private LocalDateTime endDateAfter;
    private LocalDateTime endDateBefore;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
}