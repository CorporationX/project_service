package faang.school.projectservice.repository;

import faang.school.projectservice.model.GoogleCalendarToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoogleCalendarTokenRepository extends JpaRepository<GoogleCalendarToken, Long> {
    Optional<GoogleCalendarToken> findByProjectId(@Param("projectId") long projectId);
}
