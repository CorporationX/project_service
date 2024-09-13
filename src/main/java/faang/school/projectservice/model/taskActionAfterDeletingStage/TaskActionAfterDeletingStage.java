package faang.school.projectservice.model.taskActionAfterDeletingStage;

import java.util.List;
public enum TaskActionAfterDeletingStage {
    DELETE,
    CLOSE,
    REASSIGN;

    public static List<TaskActionAfterDeletingStage> getAll() {
        return List.of(TaskActionAfterDeletingStage.values());
    }
}
