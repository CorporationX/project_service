package faang.school.projectservice.repository;

import faang.school.projectservice.model.CalendarToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CalendarTokenRepository extends JpaRepository<CalendarToken, Long> {
    @Query(nativeQuery = true, value = """
            SELECT ct.* FROM calendar_token ct
            WHERE ct.project_id = :projectId
            """)
    Optional<CalendarToken> findByProjectId(@Param("projectId") long projectId);
}
