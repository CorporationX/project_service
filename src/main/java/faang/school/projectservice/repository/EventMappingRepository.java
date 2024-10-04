package faang.school.projectservice.repository;

import faang.school.projectservice.model.EventMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventMappingRepository extends JpaRepository<EventMapping, Long> {
    Optional<EventMapping> findByEventId(Long eventId);
    Optional<EventMapping> findByGoogleEventId(String googleEventId);
}
