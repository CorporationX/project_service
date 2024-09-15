package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/stage")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @PostMapping
    public StageDto createStage(@RequestBody StageDto stageDto) {
        validateStage(stageDto);
        return stageService.createStage(stageDto);
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<StageDto>> getProjectStages(@PathVariable Long projectId,
                                                           @RequestParam(required = false) String role,
                                                           @RequestParam(required = false) String taskStatus) {
        List<StageDto> stages = stageService.getStagesByProject(projectId, role, taskStatus);
        return ResponseEntity.ok(stages);
    }

    @DeleteMapping("/{stageId}")
    public ResponseEntity<Void> deleteStage(@PathVariable Long stageId,
                                            @RequestParam(required = false) String action) {
        stageService.deleteStage(stageId, action);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{stageId}")
    public ResponseEntity<StageDto> updateStage(@PathVariable Long stageId, @RequestBody StageDto stageDto) {
        StageDto updatedStage = stageService.updateStage(stageId, stageDto);
        return ResponseEntity.ok(updatedStage);
    }

    @GetMapping("/{stageId}")
    public StageDto getStageById(@PathVariable long stageId) {
        return stageService.getStageById(stageId);
    }

    private void validateStage(StageDto stageDto) {
        if (stageDto == null) {
            throw new DataValidationException("Stage cannot be null");
        }
        if (stageDto.getProjectId() == null) {
            throw new DataValidationException("Stage project cannot be null");
        }
        if (stageDto.getStageName() == null || stageDto.getStageName().isBlank()) {
            throw new DataValidationException("Stage name cannot be null or blank");
        }
        if (stageDto.getStageRoleIds() == null || stageDto.getStageRoleIds().isEmpty()) {
            throw new DataValidationException("Stage roles cannot be null or empty");
        }
    }
}

