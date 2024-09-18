package faang.school.projectservice.model.stage.strategy.delete;

import java.util.List;
public enum DeleteStageStrategy {
    DELETE,
    CLOSE,
    REASSIGN;

    public static List<DeleteStageStrategy> getAll() {
        return List.of(DeleteStageStrategy.values());
    }
}
