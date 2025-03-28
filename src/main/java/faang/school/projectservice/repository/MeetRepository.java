package faang.school.projectservice.repository;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.stream.Stream;

public interface MeetRepository extends JpaRepository<Meet, Long> {
    @Query("""
        SELECT m FROM Meet m
        WHERE (m.project.id = :projectId) AND
        (:title IS NULL OR m.title LIKE %:title%) AND
        (:date IS NULL OR TO_CHAR(m.startsAt, 'YYYY-MM-DD') = :date)
        """)
    Stream<Meet> findByFilter(@Param("projectId") long projectId,
                              @Param("title") String title,
                              @Param("date") String date);

    Optional<Meet> findByProject(Project project);

    Optional<Meet> findByCreatorId(long creatorId);
}
