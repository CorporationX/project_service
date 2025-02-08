package faang.school.projectservice.service.enums;


import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;

import java.util.List;

public enum StrategyDelete {

    CASCADE {
        @Override
        public void execute(Stage stage, Long targetStageId,
                            StageRepository stageRepository,
                            TaskRepository taskRepository) {
            taskRepository.deleteAllByStageId(stage.getStageId());
            stageRepository.delete(stage);
        }
    },
    CLOSE {
        @Override
        public void execute(Stage stage, Long targetStageId,
                            StageRepository stageRepository,
                            TaskRepository taskRepository) {
            List<Task> tasksToClose = taskRepository
                    .findAllByStage_StageId(stage.getStageId());
            tasksToClose.forEach(Task::close);
            taskRepository.saveAll(tasksToClose);
            stageRepository.delete(stage);
        }
    },
    MOVE {
        @Override
        public void execute(Stage stage, Long targetStageId,
                            StageRepository stageRepository,
                            TaskRepository taskRepository) {
            if (targetStageId == null) {
                throw new IllegalArgumentException("Target stage ID is required for MOVE strategy");
            }
            Stage targetStage = stageRepository.findById(targetStageId)
                    .orElseThrow(() -> new DataValidationException("Target stage with id "
                            + targetStageId + " not found"));

            List<Task> tasksToMove = taskRepository.findAllByStage_StageId(stage.getStageId());
            tasksToMove.forEach(task -> task.setStage(targetStage));
            taskRepository.saveAll(tasksToMove);
            stageRepository.delete(stage);
        }
    };

    public abstract void execute(Stage stage, Long targetStageId,
                                 StageRepository stageRepository,
                                 TaskRepository taskRepository);
}
