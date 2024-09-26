package faang.school.projectservice.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;

import java.time.LocalDateTime;
import java.util.Date;

public class DateGoogleConverter {
    public static EventDateTime toEventDateTime(LocalDateTime localDateTime) {
        return new EventDateTime()
                .setDateTime(new DateTime(
                        new Date(
                                localDateTime.getYear(),
                                localDateTime.getMonth().getValue() - 1,
                                localDateTime.getDayOfMonth(),
                                localDateTime.getHour(),
                                localDateTime.getMinute()
                        )
                ));
    }

}
