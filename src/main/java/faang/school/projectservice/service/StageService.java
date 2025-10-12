package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageRequestCreateDto;
import faang.school.projectservice.dto.stage.StageRequestDeleteDto;
import faang.school.projectservice.dto.stage.StageRequestUpdateDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;

import java.util.List;

public interface StageService {

    void createStage(StageRequestCreateDto stageRequestCreateDto);

    List<Stage> getAllStageByFilter(StageRequestCreateDto stageRequestCreateDto, TeamRole teamRole, TaskStatus taskStatus);

    void deleteStage(StageRequestDeleteDto stageRequestDeleteDto);

    void updateStage(StageRequestUpdateDto stageRequestUpdateDto);

    void getAllStage();

    void getStageById();
}