package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.AllStageFilterDto;
import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDeleteDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.model.stage.Stage;

import java.util.List;

public interface StageService {

    void createStage(StageCreateDto stageCreateDto);

    List<StageDto> getAllStageByFilter(AllStageFilterDto allStageFilterDto);

    void deleteStage(StageDeleteDto stageDeleteDto);

    void updateStage(StageUpdateDto stageUpdateDto);

    List<StageDto> getStages(long projectId);

    StageDto getStage(long stageId);


}