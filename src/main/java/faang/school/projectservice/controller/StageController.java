package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static faang.school.projectservice.validate.Validate.validateId;
import static faang.school.projectservice.validate.Validate.validateStageId;
import static faang.school.projectservice.validate.Validate.validateStatus;

@RestController
@RequestMapping("/stage")
@RequiredArgsConstructor
@Validated
public class StageController {
    private final StageService stageService;

    @PostMapping("/createStage/")
    public StageDto createStage(@Valid @RequestBody StageDto stageDto) {
        validateId(stageDto);
        return stageService.createStage(stageDto);
    }

    @GetMapping("/stageStatus/")
    public List<StageDto> getAllStagesByStatus(@RequestParam(value = "status") String status) {
        validateStatus(status);
        return stageService.getAllStagesByStatus(status);
    }

    @DeleteMapping("/deleteStage/{stageId}")
    public void deleteStage(@Min(1) @PathVariable("stageId") Long stageId) {
        stageService.deleteStageById(stageId);
    }

    @PutMapping("/updateStage/")
    public StageDto updateStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.updateStage(stageDto);
    }

    @GetMapping("/getAllStages/")
    public List<StageDto> getAllStages() {
        return stageService.getAllStages();
    }

    @GetMapping("/getStageById/{stageId}")
    public StageDto getStageById(@Min(1) @PathVariable("stageId") Long stageId) {
        validateStageId(stageId);
        return stageService.getStageById(stageId);
    }
}