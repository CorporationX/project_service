package faang.school.projectservice.repository;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.model.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static faang.school.projectservice.exception.ExceptionMessages.STAGE_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class StageRepository {
    private final StageJpaRepository jpaRepository;

    public Stage save(Stage stage) {
        return jpaRepository.save(stage);
    }

    public void delete(Stage stage) {
        jpaRepository.delete(stage);
    }

    public Stage getById(Long stageId) {
        return jpaRepository.findById(stageId).orElseThrow(
                () -> new EntityNotFoundException(String.format(STAGE_NOT_FOUND.getMessage(), stageId))
        );
    }

    public List<Stage> findAll() {
        return jpaRepository.findAll();
    }
}
