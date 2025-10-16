package faang.school.projectservice.config.stage;

import faang.school.projectservice.model.stage.enums.DeleteStrategy;
import faang.school.projectservice.service.stage.deletion.StageDeletionStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

/**
 * Конфигурационный класс для настройки стратегий удаления этапов проекта.
 * <p>
 * Создает и настраивает соответствие между типами стратегий удаления ({@link DeleteStrategy})
 * и их реализациями ({@link StageDeletionStrategy}). Использует {@link EnumMap} для
 * эффективного хранения и доступа к стратегиям.
 * </p>
 *
 * @author bozya
 * @since 10.08.2025
 */
@Configuration
public class StageDeletionConfig {

    @Bean
    public Map<DeleteStrategy, StageDeletionStrategy> strategyMap(
            @Qualifier("cascadeDeletionStrategy") StageDeletionStrategy cascade,
            @Qualifier("closeDeletionStrategy") StageDeletionStrategy close,
            @Qualifier("moveDeletionStrategy") StageDeletionStrategy move) {

        Map<DeleteStrategy, StageDeletionStrategy> strategies = new EnumMap<>(DeleteStrategy.class);
        strategies.put(DeleteStrategy.CASCADE, cascade);
        strategies.put(DeleteStrategy.CLOSE, close);
        strategies.put(DeleteStrategy.MOVE, move);
        return strategies;
    }
}