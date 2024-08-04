package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDeleteTaskStrategyDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.teamrole.TeamRoleDto;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/stage")
public class StageController {
    private final StageService stageService;

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public StageDto createStage(@RequestBody @Valid StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @GetMapping(value = "{projectId}/filter")
    public List<StageDto> filterStages(@PathVariable Long projectId,
                                       @RequestBody StageFilterDto stageFilterDto) {
        return stageService.filterStages(projectId, stageFilterDto);
    }

    @PutMapping(value = "/{stageId}/executors")
    @ResponseStatus(HttpStatus.OK)
    public void updateStage(@PathVariable Long stageId,
                            @RequestBody TeamRoleDto teamRoleDto) {
        stageService.updateStageWithTeamMembers(stageId, teamRoleDto);
    }

    @DeleteMapping(value = "/{stageToDeleteId}/{newStageId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteStage(@PathVariable Long stageToDeleteId,
                            @RequestBody @Valid StageDeleteTaskStrategyDto strategyDto,
                            @PathVariable(required = false) Long newStageId) {
        stageService.deleteStage(stageToDeleteId, strategyDto, newStageId);
    }

    @GetMapping(value = "all/{projectId}")
    public List<StageDto> getAllStageByProjectId(@PathVariable Long projectId) {
        return stageService.getAllStageByProjectId(projectId);
    }

    @GetMapping(value = "/{stageId}")
    public StageDto getStageById(@PathVariable Long stageId) {
        return stageService.getStageById(stageId);
    }
}
