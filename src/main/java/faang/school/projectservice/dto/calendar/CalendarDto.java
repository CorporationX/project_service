package faang.school.projectservice.dto.calendar;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarDto {

    private String id;
    private String description;
    private String location;

    @NotBlank
    private String summary;
}
