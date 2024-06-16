package faang.school.projectservice.pattern.strategy.stage;

public interface StrategyForDeletingStage {
    void manageTasksOfStage(long stageId, Long stageToMigrateId);
}
