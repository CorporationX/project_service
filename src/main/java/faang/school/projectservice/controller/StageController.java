package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.model.stage.StageStatus;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController("/stage")
@RequiredArgsConstructor
@Validated
public class StageController {
    private final StageService stageService;

    @PostMapping("/createStage")
    public StageDto createStage(@Valid @RequestBody StageDto stageDto) {
        if (stageDto.getStageId() != null || stageDto.getStageRolesDto().stream().anyMatch(stageRolesDto -> stageRolesDto.getId() != null)) {
            throw new IllegalArgumentException("Id must be null");
        }
        return stageService.createStage(stageDto);
    }

    @GetMapping("/stageStatus/")
    public List<StageDto> getAllStagesByStatus(@RequestParam(value = "status") String status) {
        if(Arrays.stream(StageStatus.values()).noneMatch(stageStatus -> stageStatus.toString().equalsIgnoreCase(status))) {
            throw new IllegalArgumentException("Invalid status");
        }
        return stageService.getAllStagesByStatus(status);
    }

    @PutMapping("/updateStage")
    public StageDto updateStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.updateStage(stageDto);
    }

    @DeleteMapping("/deleteStage")
    public void deleteStage(@Valid @RequestBody StageDto stageDto) {
        stageService.deleteStage(stageDto);
    }

    @GetMapping("/getAllStages")
    public List<StageDto> getAllStages() {
        return stageService.getAllStages();
    }

    @GetMapping("/getStageById/{stageId}")
    public StageDto getStageById(@Valid @PathVariable("stageId") Long stageId) {
        return stageService.getStageById(stageId);
    }
}