package faang.school.projectservice.repository;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetRepository extends JpaRepository<Meet, Long> {

    Optional<Meet> findByProject(Project project);

    Optional<Meet> findByCreatorId(long creatorId);

    default Meet getByIdOrThrow(long meetId) {
        return findById(meetId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Meet %d not found", meetId))
        );
    }
}
