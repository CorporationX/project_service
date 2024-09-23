package faang.school.projectservice.service.calendar;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface CalendarService {
    void create();
    void update();
    void view() throws GeneralSecurityException, IOException;
}
