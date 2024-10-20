package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetRepository extends JpaRepository<Meet, Long> {

    Optional<Meet> findByProject(Project project);

    Optional<Meet> findByCreatorId(long creatorId);

    List<Meet> findAllByStatus(MeetStatus status);

    List<Meet> findAllByStatusIn(List<MeetStatus> statuses);

    @Query(value = "SELECT * FROM meet m WHERE (:title IS NULL OR m.title LIKE %:title%) " +
            "AND (:date IS NULL OR CAST(m.created_at AS DATE) = CAST(:date AS DATE))", nativeQuery = true)
    List<Meet> findAllMeetingsByTitleAndDate(@Param("title") String title, @Param("date") String date);
}
