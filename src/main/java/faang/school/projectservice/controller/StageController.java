package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.service.StageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
@AllArgsConstructor
public class StageController {
    private final StageService stageService;

    public void makeStage(String stageName, Long projectId, List<StageRoles> stageRoles){
        if (stageName.isEmpty()) {
            throw new DataValidationException("Stage name can not be empty!");
        }
        if (stageRoles.isEmpty()) {
            throw new DataValidationException("Stage roles can not be empty!");
        }
        stageService.makeStage(stageName, projectId, stageRoles);
    }

    private List<StageDto> getStagesByStatus(Long projectId, TaskStatus status) {
        return stageService.getStagesByStatus(projectId, status);
    }

    private void deleteStage(Long stageId) {
        stageService.deleteStage(stageId);
    }

    private void deleteStage(Long stageIdToDelete, Long stageIdToReceive) {
        stageService.deleteStage(stageIdToDelete, stageIdToReceive);
    }

    private void updateStage(Long stageId) {
        stageService.updateStage(stageId);
    }

    private List<StageDto> getAllStages(Long projectId) {
        return stageService.getAllStages(projectId);
    }

    private StageDto getStage(Long stageId) {
        return stageService.getStage(stageId);
    }

}
