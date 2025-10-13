package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageRequestAllStageDto;
import faang.school.projectservice.dto.stage.StageRequestCreateDto;
import faang.school.projectservice.dto.stage.StageRequestDeleteDto;
import faang.school.projectservice.dto.stage.StageRequestUpdateDto;
import faang.school.projectservice.model.stage.Stage;

import java.util.List;

public interface StageService {

    void createStage(StageRequestCreateDto stageRequestCreateDto);

    List<Stage> getAllStageByFilter(StageRequestAllStageDto stageRequestAllStageDto);

    void deleteStage(StageRequestDeleteDto stageRequestDeleteDto);

    void updateStage(StageRequestUpdateDto stageRequestUpdateDto);

    List<Stage> getAllStage(long projectId);

    Stage getStageById(long stageId);
}