package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/project-stages")
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;
    private final StageValidator stageValidator;

    @PostMapping
    public StageDto createStage(@RequestBody StageDto stage) {
        stageValidator.validateStage(stage);
        return stageService.createStage(stage);
    }

    @GetMapping
    public List<StageDto> getAllStageByFilter(@RequestBody StageFilterDto filters) {
        return stageService.getAllStageByFilter(filters);
    }

    @DeleteMapping("/{id}")
    public void deleteStageById(@PathVariable("id") Long stageId) {
        stageValidator.validateNullStageId(stageId);
        stageService.deleteStageById(stageId);
    }

    @GetMapping("/{id}")
    public StageDto getStageById(@PathVariable("id") Long stageId) {
        stageValidator.validateNullStageId(stageId);
        return stageService.getStagesById(stageId);
    }

    @PutMapping
    public StageDto updateStage(Long stageId) {
        stageValidator.validateNullStageId(stageId);
        return stageService.updateStage(stageId);
    }
}