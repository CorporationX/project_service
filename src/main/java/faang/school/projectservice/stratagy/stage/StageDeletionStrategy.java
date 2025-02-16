package faang.school.projectservice.stratagy.stage;

import faang.school.projectservice.model.stage.Stage;

public interface StageDeletionStrategy {
    void handleTasksBeforeStageDeletion(Stage stage);

    StageDeletionType getName();
}
