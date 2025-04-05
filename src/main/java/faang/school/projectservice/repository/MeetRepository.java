package faang.school.projectservice.repository;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetRepository extends JpaRepository<Meet, Long> {

    Optional<Meet> findByProject(Project project);

    Optional<Meet> findByCreatorId(long creatorId);

    Optional<Meet> findById(long id);

    List<Meet> findByTitleContainingIgnoreCase(String title);

    List<Meet> findByStartsAt(LocalDateTime startDate);

    List<Meet> findByTitleContainingIgnoreCaseAndStartsAt(String title, LocalDateTime startDate);
}
