package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.StagePreDestroyAction;
import faang.school.projectservice.service.stage.StageService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/stage")
@Controller
public class StageController {
    private final StageService stageService;

    @PostMapping("/add")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public StageDto addStage(@RequestBody StageDto stageDto) {
        return stageService.create(stageDto);
    }

    @GetMapping("/stages/{projectId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<StageDto> getStages(@PathVariable Long projectId, @RequestBody StageFilterDto filter) {
        return stageService.getStagesByFilter(projectId, filter);
    }

    @DeleteMapping("/remove/{stageId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeStage(@PathVariable Long stageId, @RequestBody StagePreDestroyAction action) {
        stageService.removeStage(stageId, action);
    }

    @DeleteMapping("/remove/{stageId}/{replaceStageId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeStage(@PathVariable Long stageId, @PathVariable Long replaceStageId) {
        stageService.removeStage(stageId, replaceStageId);
    }

    @PutMapping("/update/{stageId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateStage(@PathVariable Long stageId) {
        stageService.updateStage(stageId);
    }

    @GetMapping("/stages/all/{projectId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<StageDto> getAllStages(@PathVariable Long projectId) {
        return stageService.getAllStages(projectId);
    }

    @GetMapping("/{stageId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public StageDto getStage(@PathVariable Long stageId) {
        return stageService.getStage(stageId);
    }
}