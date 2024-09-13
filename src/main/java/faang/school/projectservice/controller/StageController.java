package faang.school.projectservice.controller;

import faang.school.projectservice.controller.validation.*;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.taskActionAfterDeletingStage.TaskActionAfterDeletingStage;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.StageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class StageController {
    private final StageService stageService;
    private final EntityValidator entityValidator;
    private final StageValidator stageValidator;
    private final ProjectValidator projectValidator;
    private final TaskStatusValidator taskStatusValidator;
    private final TeamRoleValidator teamRoleValidator;
    private final StageRolesValidator stageRolesValidator;
    private final TaskActionsAfterDeletingStageValidator taskActionValidator;

    @PostMapping("/stage")
    public StageDto createStage(@RequestBody StageDto stageDto) {
        entityValidator.validateIdForIncorrect(stageDto.projectId(), "Project");
        entityValidator.validateStringAtributeForIncorrect(stageDto.stageName(), "Stage name");
        stageRolesValidator.validateRolesWithAmount(stageDto.rolesWithAmount());

        return stageService.createStage(stageDto);
    }

    @GetMapping("/stage/filters")
    public List<StageDto> getFilteredStagesByRolesAndStatus(
            @RequestParam Long projectId,
            @RequestParam List<TeamRole> roles,
            @RequestParam List<TaskStatus> taskStatuses) {

        entityValidator.validateIdForIncorrect(projectId, "Project");
        roles.forEach(teamRoleValidator::validateIsItTeamRole);
        taskStatuses.forEach(taskStatusValidator::validateIsItTaskStatus);

        return stageService.getFilteredStagesByRolesAndStatus(projectId, roles, taskStatuses);
    }

    @DeleteMapping("/stage/{stageId}")
    public void deleteStage(@PathVariable Long stageId,
                            @RequestParam TaskActionAfterDeletingStage taskAction) {

        entityValidator.validateIdForIncorrect(stageId, "Stage");
        taskActionValidator.validateIsItTaskActionsAfterDeletingStage(taskAction);

        stageService.deleteStage(stageId, taskAction);
    }

    @GetMapping("/project/{projectId}/stages")
    public List<StageDto> getAllStagesByProjectId(@PathVariable Long projectId) {
        entityValidator.validateIdForIncorrect(projectId, "Project");
        return stageService.getAllStagesByProjectId(projectId);
    }


    @GetMapping("stage/{stageId}")
    public StageDto getStageById(@PathVariable Long stageId) {
        entityValidator.validateIdForIncorrect(stageId, "Stage");
        return stageService.getStageById(stageId);
    }



}
