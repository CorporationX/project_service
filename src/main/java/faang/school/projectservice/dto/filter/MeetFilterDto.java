package faang.school.projectservice.dto.filter;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class MeetFilterDto {

    @Size(max = 128)
    private String name;

    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;
}
