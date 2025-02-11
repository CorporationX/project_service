package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageCreateRequestDto;
import faang.school.projectservice.dto.stage.StageCreateResponseDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageResponseDto;
import faang.school.projectservice.dto.stage.StageUpdateRequestDto;
import faang.school.projectservice.dto.stage.StageUpdateResponseDto;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.stratagy.stage.StageDeletionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/stages")
@Validated
public class StageController {
    private final StageService stageService;

    @PostMapping
    public StageCreateResponseDto createStage(@RequestBody StageCreateRequestDto stageCreateRequestDto) {
        return stageService.createStage(stageCreateRequestDto);
    }

    @GetMapping("/project/{projectId}")
    public List<StageResponseDto> getAllProjectStages(@PathVariable Long projectId, StageFilterDto stageFilterDto) {
        return stageService.getAllProjectStages(projectId, stageFilterDto);
    }

    @DeleteMapping("/{stageId}")
    public ResponseEntity<Void> deleteStage(@PathVariable Long stageId, @RequestParam StageDeletionType deletionType) {
        stageService.deleteStage(stageId, deletionType);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public StageUpdateResponseDto updateStage(@RequestBody StageUpdateRequestDto stageUpdateRequestDto) {
        return stageService.updateStage(stageUpdateRequestDto);
    }

    @GetMapping("/{stageId}")
    public StageResponseDto getStageById(@PathVariable Long stageId) {
        return stageService.getStageDtoById(stageId);
    }
}
