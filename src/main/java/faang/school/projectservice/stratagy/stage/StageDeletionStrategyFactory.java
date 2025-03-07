package faang.school.projectservice.stratagy.stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StageDeletionStrategyFactory {
    private final Map<StageDeletionType, StageDeletionStrategy> strategies  = new HashMap<>();

    @Autowired
    public StageDeletionStrategyFactory(List<StageDeletionStrategy> strategiesList) {
        for (StageDeletionStrategy stageDeletionStrategy : strategiesList) {
            strategies.put(stageDeletionStrategy.getName(), stageDeletionStrategy);
        }
    }

    public StageDeletionStrategy getStrategy(StageDeletionType type) {
        return strategies.getOrDefault(type, strategies.get(StageDeletionType.MOVE_TASKS));
    }
}