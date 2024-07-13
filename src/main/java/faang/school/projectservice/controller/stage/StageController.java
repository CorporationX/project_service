package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.StagePreDestroyAction;
import faang.school.projectservice.service.stage.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/stage")
@Controller
public class StageController {
    private final StageService stageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageDto addStage(StageDto stageDto) {
        return stageService.create(stageDto);
    }

    @GetMapping("/stages")
    @ResponseStatus(HttpStatus.OK)
    public List<StageDto> getStages(Long projectId, StageFilterDto filter) {
        return stageService.getStages(projectId, filter);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void removeStagess(Long stageId, StagePreDestroyAction action) {
        stageService.removeStage(stageId, action);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void removeStagess(Long stageId, Long replaceStageId) {
        stageService.removeStagess(stageId, replaceStageId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateStage(Long stageId) {
        stageService.updateStage(stageId);
    }

    @GetMapping("/stages")
    @ResponseStatus(HttpStatus.OK)
    public List<StageDto> getAllStages(Long projectId) {
        return stageService.getAllStages(projectId);
    }

    @GetMapping("{stageId}")
    @ResponseStatus(HttpStatus.OK)
    public StageDto getStage(Long stageId) {
        return stageService.getStage(stageId);
    }
}