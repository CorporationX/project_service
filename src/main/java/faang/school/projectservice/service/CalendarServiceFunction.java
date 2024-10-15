package faang.school.projectservice.service;



import com.google.api.services.calendar.Calendar;

import java.io.IOException;

@FunctionalInterface
public interface CalendarServiceFunction<T> {
    T execute(Calendar service) throws IOException;
}
