package faang.school.projectservice.util;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Calendar;

@UtilityClass
public class DateGoogleConverter {
    public static EventDateTime toEventDateTime(LocalDateTime localDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(localDateTime.getYear(), localDateTime.getMonthValue() - 1,
                localDateTime.getDayOfMonth(), localDateTime.getHour(), localDateTime.getMinute(),
                localDateTime.getSecond());
        return new EventDateTime()
                .setDateTime(new DateTime(calendar.getTime()));
    }
}
