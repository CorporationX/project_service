package faang.school.projectservice.service.Stage;

import faang.school.projectservice.model.stage.Stage;

public interface StageDeletionStrategy {
    void deleteStage(Stage stage, Stage target);

    boolean requiresTargetStage();
}
