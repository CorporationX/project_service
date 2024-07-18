package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.StageDeleteTaskStrategyDto;
import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.dto.client.StageFilterDto;
import faang.school.projectservice.dto.client.TeamRoleDto;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/stages")
public class StageController {
    private final StageService stageService;

    @Autowired
    public StageController(StageService stageService) {
        this.stageService = stageService;
    }

    @PostMapping(value = "/create/stage")
    @ResponseStatus(HttpStatus.CREATED)
    public void createStage(@RequestBody StageDto stageDto) {
        stageService.createStage(stageDto);
    }

    @GetMapping(value = "{projectId}/filter/stages")
    public List<StageDto> filterStages(@PathVariable Long projectId,
                                       @RequestBody StageFilterDto stageFilterDto) {
        return stageService.filterStages(projectId, stageFilterDto);
    }

    @GetMapping(value = "/update/{stageId}/with/executors")
    @ResponseStatus(HttpStatus.OK)
    public void updateStage(@PathVariable Long stageId,
                            @RequestBody TeamRoleDto teamRoleDto) {
        stageService.updateStageWithTeamMembers(stageId, teamRoleDto);
    }

    @PostMapping(value = "/delete/stage/{stageToDeleteId}/{newStageId}")
    public StageDeleteTaskStrategyDto deleteStage(@PathVariable Long stageToDeleteId,
                                                  @RequestBody @Valid StageDeleteTaskStrategyDto strategyDto,
                                                  @PathVariable(required = false) Long newStageId) {
        return stageService.deleteStage(stageToDeleteId, strategyDto, newStageId);
    }

    @GetMapping(value = "get/stages/in/{projectId}")
    public List<StageDto> getAllStageByProjectId(@PathVariable Long projectId) {
        return stageService.getAllStageByProjectId(projectId);
    }

    @GetMapping(value = "get/{stageId}/stage")
    public StageDto getStageById(@PathVariable Long stageId) {
        return stageService.getStageById(stageId);
    }
}
