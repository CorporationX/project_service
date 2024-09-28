package faang.school.projectservice.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

class DateGoogleConverterTest {
    private LocalDateTime testTime1 = LocalDateTime.of(2005, Month.AUGUST, 15, 17, 30);
    private LocalDateTime testTime2 = LocalDateTime.of(2020, Month.SEPTEMBER, 1, 13, 0);
    private LocalDateTime testTime3 = LocalDateTime.of(2000, Month.DECEMBER, 31, 23, 59);

    private EventDateTime expectedTime1 = new EventDateTime()
            .setDateTime(new DateTime(new Date(2005, Calendar.AUGUST, 15, 17, 30)));
    private EventDateTime expectedTime2 = new EventDateTime()
            .setDateTime(new DateTime(new Date(2020, Calendar.SEPTEMBER, 1, 13, 0)));
    private EventDateTime expectedTime3 = new EventDateTime()
            .setDateTime(new DateTime(new Date(2000, Calendar.DECEMBER, 31, 23, 59)));
    @Test
    void toEventDateTimeTest() {
        Assertions.assertEquals(expectedTime1, DateGoogleConverter.toEventDateTime(testTime1));
        Assertions.assertEquals(expectedTime2, DateGoogleConverter.toEventDateTime(testTime2));
        Assertions.assertEquals(expectedTime3, DateGoogleConverter.toEventDateTime(testTime3));
    }
}
