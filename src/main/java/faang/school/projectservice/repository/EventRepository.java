package faang.school.projectservice.repository;

import faang.school.projectservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM Event e JOIN e.project p " +
            "WHERE p.id = :projectId AND e.id = :eventId")
    boolean existsByProjectIdAndEventId(Long projectId, Long eventId
    );
}