package faang.school.projectservice.dto.project.calendar;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalendarDto {
    private String id;
    @NotNull
    private String summary;
    private String description;
    private String location;
}
