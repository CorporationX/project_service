package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeetRepository extends JpaRepository<Meet, Long> {

    Optional<Meet> findByProject(Project project);

    Optional<Meet> findByCreatorId(long creatorId);

    @Query("""
            from Meet m where
            m.title like :titlePattern
            and m.date >= :minDate
            and m.date <= :maxDate
            and m.project.id = :projectId
            """)
    List<Meet> findAllForProjectFilterByTitlePatternAndDateRange(String titlePattern,
                                                                 LocalDateTime minDate,
                                                                 LocalDateTime maxDate,
                                                                 long projectId);

    @Query("""
            from Meet m where
            m.project.id = :projectId
            """)
    List<Meet> findAllByProject(long projectId);
}
