package faang.school.projectservice.service.stage.deletion;

import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

/**
 * StageDeletionStrategy — Интерфейс стратегии для различных способов удаления этапа проекта
 *
 * @author bozya
 * @since 06.08.2025
 */
@Component
public interface StageDeletionStrategy {
    void delete(Stage stage);
}