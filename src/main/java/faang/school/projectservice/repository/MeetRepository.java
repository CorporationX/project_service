package faang.school.projectservice.repository;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.NoSuchElementException;
import java.util.Optional;

public interface MeetRepository extends JpaRepository<Meet, Long>, JpaSpecificationExecutor<Meet> {

    Optional<Meet> findByProject(Project project);

    Optional<Meet> findByCreatorId(long creatorId);

    default Meet findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NoSuchElementException(
                String.format("Meet id %d not found", id)));
    }
}
