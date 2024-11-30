package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageFilterDto;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/stages")
@RequiredArgsConstructor
@Slf4j
@Validated
public class StageController {

    private final StageService stageService;

    @PostMapping
    public ResponseEntity<StageDto> createStage(
            @Valid @RequestBody StageDto stageDto
    ) {
        log.info("Received request to create stage: {}", stageDto);
        StageDto createdStage = stageService.createStage(stageDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdStage);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<StageDto>> getStagesByRolesAndTaskStatuses(
            StageFilterDto filter
    ) {
        List<StageDto> stages = stageService
                .getStagesByRolesAndTaskStatuses(filter);
        return ResponseEntity.ok(stages);
    }

    @GetMapping
    public ResponseEntity<List<StageDto>> getAllStages() {
        List<StageDto> stages = stageService.getAllStages();
        return ResponseEntity.ok(stages);
    }

    @GetMapping("/{stageId}")
    public ResponseEntity<StageDto> getStageById(
            @PathVariable Long stageId
    ) {
        StageDto stage = stageService.getStageById(stageId);
        return ResponseEntity.ok(stage);
    }

    @PutMapping
    public ResponseEntity<StageDto> updateStage(
            @Valid @RequestBody StageDto updatedStageDto
    ) {
        log.info("Request received to update stage with ID: {}",
                updatedStageDto.getStageId());
        StageDto updatedStage = stageService
                .updateStage(updatedStageDto);
        return ResponseEntity.ok(updatedStage);
    }

    @DeleteMapping("/{stageId}")
    public ResponseEntity<Void> deleteStage(
            @PathVariable Long stageId,
            @RequestParam String taskAction,
            @RequestParam(required = false) Long targetStageId
    ) {
        stageService.deleteStage(stageId, taskAction, targetStageId);
        return ResponseEntity.noContent().build();
    }
}
