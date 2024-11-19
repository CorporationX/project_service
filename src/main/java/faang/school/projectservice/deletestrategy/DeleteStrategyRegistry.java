package faang.school.projectservice.deletestrategy;

import faang.school.projectservice.entity.stage.DeleteStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DeleteStrategyRegistry {
    private final Map<DeleteStrategy, DeleteStrategyExecutor> deleteStrategyExecutors;

    public DeleteStrategyRegistry(List<DeleteStrategyExecutor> deleteStrategies) {
        this.deleteStrategyExecutors = deleteStrategies.stream()
                .collect(Collectors.toMap(
                        DeleteStrategyExecutor::getStrategyType,
                        Function.identity()));
    }

    public DeleteStrategyExecutor getExecutor(DeleteStrategy strategy) {
        return deleteStrategyExecutors.get(strategy);
    }

    public Map<DeleteStrategy, DeleteStrategyExecutor> getAllExecutors() {
        return deleteStrategyExecutors;
    }
}