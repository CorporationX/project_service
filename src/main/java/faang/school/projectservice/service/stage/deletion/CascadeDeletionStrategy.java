package faang.school.projectservice.service.stage.deletion;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * CascadeDeletionStrategy — Стратегия полного каскадного удаления этапа вместе с задачами.
 *
 * @author bozya
 * @since 06.08.2025
 */
@Slf4j
@Service("cascadeDeletionStrategy")
@RequiredArgsConstructor
public class CascadeDeletionStrategy implements StageDeletionStrategy {
    private final StageRepository stageRepository;

    @Override
    @Transactional
    public void delete(Stage stage) {
        stageRepository.delete(stage);

        log.info("Stage {} and all its tasks were cascade-deleted", stage.getStageId());
    }
}