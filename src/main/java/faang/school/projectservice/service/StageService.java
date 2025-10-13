package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.CreateStageDto;
import faang.school.projectservice.dto.client.ProjectIdDto;
import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.dto.client.StageIdDto;
import faang.school.projectservice.dto.client.UpdateStageDto;

import java.util.List;

public interface StageService {
    StageDto createStage(CreateStageDto stageDto);

    List<StageDto> getAllStagesOfProject(ProjectIdDto projectIdDto);

    void deleteById(StageIdDto stageIdDto);

    StageDto updateStage(UpdateStageDto stageDto);

    StageDto getById(StageIdDto stageIdDto);
}
