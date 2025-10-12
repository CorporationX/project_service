package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageRequestDeleteDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;

import java.util.List;

public interface StageService {

    void createStage(StageCreateDto stageCreateDto);

    List<Stage> getAllStageByFilter(StageCreateDto stageCreateDto, TeamRole teamRole, TaskStatus taskStatus);

    void deleteStage(StageRequestDeleteDto stageRequestDeleteDto);

    void updateStage();

    void getAllStage();

    void getStageById();
}