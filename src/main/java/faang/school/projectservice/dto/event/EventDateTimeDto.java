package faang.school.projectservice.dto.event;

import faang.school.projectservice.validation.ValidDateTimeString;
import faang.school.projectservice.validation.ValidTimeZone;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EventDateTimeDto {
    @NotNull(message = "DateTime could not be null")
    @ValidDateTimeString(message = "date time is not in correct format")
    private String dateTime;

    @NotNull(message = "Timezone could not be null")
    @ValidTimeZone(message = "Timezone is not in correct format")
    private String timeZone;
}