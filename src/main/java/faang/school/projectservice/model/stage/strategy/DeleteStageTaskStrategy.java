package faang.school.projectservice.model.stage.strategy;

import java.util.List;

public enum DeleteStageTaskStrategy {
    DELETE,
    CLOSE,
    REASSIGN;

    public static List<DeleteStageTaskStrategy> getAll() {
        return List.of(DeleteStageTaskStrategy.values());
    }
}
