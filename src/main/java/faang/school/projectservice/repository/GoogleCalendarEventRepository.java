package faang.school.projectservice.repository;

import faang.school.projectservice.model.google.calendar.GoogleCalendarEvent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GoogleCalendarEventRepository extends CrudRepository<GoogleCalendarEvent, Long> {
    List<GoogleCalendarEvent> findAllByIsCanceledFalse();
}

