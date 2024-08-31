package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.StagePreDestroyAction;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.stage.StageRolesService;
import faang.school.projectservice.service.stage.StageService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/stage")
@RestController
public class StageController {
    private final StageService stageService;
    private final StageRolesService stageRolesService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public StageDto addStage(@RequestBody StageDto stageDto) {
        return stageService.create(stageDto);
    }

    @GetMapping("/stages/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public List<StageDto> getStages(@PathVariable long projectId, @RequestBody StageFilterDto filter) {
        return stageService.getStagesByFilter(projectId, filter);
    }

    @DeleteMapping("/remove/{stageId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeStage(@PathVariable long stageId, @RequestBody StagePreDestroyAction action) {
        stageService.removeStage(stageId, action);
    }

    @DeleteMapping("/remove/{stageId}/{replaceStageId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeStageAndReplaceTasks(@PathVariable Long stageId, @PathVariable long replaceStageId) {
        stageService.removeStageAndReplaceTasks(stageId, replaceStageId);
    }

    @GetMapping("/roles/deficit/{stageId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Integer> getAllRolesDeficit(@PathVariable long stageId) {
        return stageRolesService.getAllRolesDeficit(stageId);
    }

    @PutMapping("/update/{stageId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateStage(@PathVariable long stageId) {
        stageService.updateStage(stageId);
    }

    @GetMapping("/stages/all/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public List<StageDto> getAllStages(@PathVariable long projectId) {
        return stageService.getAllStages(projectId);
    }

    @GetMapping("/{stageId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public StageDto getStage(@PathVariable long stageId) {
        return stageService.getStage(stageId);
    }

    @PutMapping("/roles/add")
    public void addStageRoles(StageRolesDto stageRolesDto) {
        stageRolesService.addStageRoles(stageRolesDto);
    }

    @PutMapping("/roles/set")
    public void setStageRoles(StageRolesDto stageRolesDto) {
        stageRolesService.setStageRoles(stageRolesDto);
    }

    @GetMapping("/roles/get/{stageId}")
    public Map<TeamRole, Integer> getAllStageRoles(@PathVariable long stageId) {
        return stageRolesService.getAllStageRoles(stageId);
    }
}