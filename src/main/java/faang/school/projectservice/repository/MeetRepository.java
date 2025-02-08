package faang.school.projectservice.repository;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.stream.Stream;

public interface MeetRepository extends JpaRepository<Meet, Long> {

    Optional<Meet> findByProject(Project project);

    Optional<Meet> findByCreatorId(long creatorId);

    @Query(nativeQuery = true, value = """
            SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
            FROM meet
            WHERE id = ?1 AND creator_id = ?2
            """)
    boolean isUserOwnerMeet(long meetId, long userId);

    Stream<Meet> findByProjectId(long projectId);
}
