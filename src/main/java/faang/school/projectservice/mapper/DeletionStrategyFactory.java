package faang.school.projectservice.mapper;

import faang.school.projectservice.service.Stage.CascadeDeleteStrategy;
import faang.school.projectservice.service.Stage.CloseTasksStrategy;
import faang.school.projectservice.service.Stage.MoveTasksStrategy;
import faang.school.projectservice.service.Stage.StageDeletionStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DeletionStrategyFactory {
    private final MoveTasksStrategy moveTasksStrategy;
    private final CloseTasksStrategy closeTasksStrategy;
    private final CascadeDeleteStrategy cascadeDeleteStrategy;

    private final Map<String, StageDeletionStrategy> strategyMap = new HashMap<>();

    @PostConstruct
    public void init() {
        strategyMap.put("MOVE_TASKS", moveTasksStrategy);
        strategyMap.put("CLOSE_TASKS", closeTasksStrategy);
        strategyMap.put("CASCADE_DELETE", cascadeDeleteStrategy);
    }

    public StageDeletionStrategy getStrategy(String strategyName) {
        StageDeletionStrategy strategy = strategyMap.get(strategyName.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown deletion strategy: " + strategyName);
        }
        return strategy;
    }
}
