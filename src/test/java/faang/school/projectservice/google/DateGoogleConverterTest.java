package faang.school.projectservice.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.util.DateGoogleConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

class DateGoogleConverterTest {
    private LocalDateTime testTime1 = LocalDateTime.of(2005, Month.AUGUST, 15, 17, 30, 10);
    private LocalDateTime testTime2 = LocalDateTime.of(2020, Month.SEPTEMBER, 1, 13, 0, 10);
    private LocalDateTime testTime3 = LocalDateTime.of(2000, Month.DECEMBER, 31, 23, 59, 10);
    private EventDateTime expectedTime1 = new EventDateTime();
    private EventDateTime expectedTime2 = new EventDateTime();
    private EventDateTime expectedTime3 = new EventDateTime();

    @BeforeEach
    public void init() {
        Calendar expected1 = Calendar.getInstance();
        expected1.set(2005, Calendar.AUGUST, 15, 17, 30, 10);
        Calendar expected2 = Calendar.getInstance();
        expected2.set(2020, Calendar.SEPTEMBER, 1, 13, 0, 10);
        Calendar expected3 = Calendar.getInstance();
        expected3.set(2000, Calendar.DECEMBER, 31, 23, 59, 10);

        expectedTime1.setDateTime(new DateTime(expected1.getTime()));
        expectedTime2.setDateTime(new DateTime(expected2.getTime()));
        expectedTime3.setDateTime(new DateTime(expected3.getTime()));
    }

    @Test
    void toEventDateTimeTest() {
        Assertions.assertTrue(isSameDay(expectedTime1.getDateTime(),
                DateGoogleConverter.toEventDateTime(testTime1).getDateTime()));
        Assertions.assertTrue(isSameDay(expectedTime2.getDateTime(),
                DateGoogleConverter.toEventDateTime(testTime2).getDateTime()));
        Assertions.assertTrue(isSameDay(expectedTime2.getDateTime(),
                DateGoogleConverter.toEventDateTime(testTime2).getDateTime()));
    }

    public static boolean isSameDay(DateTime date1, DateTime date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(new Date(date1.getValue())).equals(fmt.format(new Date(date2.getValue())));
    }
}
