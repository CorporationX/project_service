package faang.school.projectservice.repository;

import faang.school.projectservice.model.stage.Stage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageRepository extends JpaRepository<Stage, Long> {
    default Stage getByIdOrThrow(long stageId) {
        return findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(stageId)));
    }
}
