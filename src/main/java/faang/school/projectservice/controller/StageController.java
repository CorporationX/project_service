package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageFilterDto;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stage")
@Validated
public class StageController {
    private final StageService stageService;

    @PostMapping("/createStage")
    public StageDto createStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @PatchMapping("/{updateStage}")
    public StageDto updateStage(StageDto stageDto) {
        return stageService.updateStage(stageDto);
    }

    @GetMapping("/{stageStatus}")
    public List<StageDto> getAllStagesByStatus(StageFilterDto stageFilterDto) {
        return stageService.getAllStagesByStatus(stageFilterDto);
    }

    @DeleteMapping("/{stage}")
    public void deleteStage(StageDto stageDto) {
        stageService.deleteStage(stageDto);
    }

    @GetMapping("/{allStages}")
    public List<StageDto> getAllStages() {
        return stageService.getAllStages();
    }

    @GetMapping("/{id}")
    public StageDto getStageById(long stageId) {
        return stageService.getStageById(stageId);
    }
}