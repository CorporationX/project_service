package faang.school.projectservice.dto.event;

import faang.school.projectservice.validation.ValidDateTimeString;
import faang.school.projectservice.validation.ValidTimeZone;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

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

    public LocalDateTime toLocalDateTime() {
        if (this.dateTime == null || this.timeZone == null) {
            throw new IllegalArgumentException("DateTime or TimeZone is null");
        }

        try {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(this.dateTime);
            ZoneId targetZoneId = ZoneId.of(this.timeZone);
            ZonedDateTime zonedDateTimeInTargetZone = offsetDateTime.atZoneSameInstant(targetZoneId);

            return zonedDateTimeInTargetZone.toLocalDateTime();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date time format %s".formatted(dateTime), e);
        } catch (java.time.DateTimeException e) {
            throw new IllegalArgumentException("Invalid time zone format %s".formatted(timeZone), e);
        }
    }
}