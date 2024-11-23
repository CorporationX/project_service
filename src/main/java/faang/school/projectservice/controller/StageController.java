package faang.school.projectservice.controller;

import faang.school.projectservice.dto.TeamMemberDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stages")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @GetMapping("{projectId}")
    public ResponseEntity<List<StageDto>> getAllStagesByProjectId(@PathVariable @Positive long projectId) {
        return ResponseEntity.ok(stageService.getStagesByProjectId(projectId));
    }

    @GetMapping("{projectId}/filter")
    public ResponseEntity<List<StageDto>> getFilteredStagesByProjectId(@PathVariable @Positive long projectId,
                                                                       @RequestBody StageFilterDto stageFilterDto) {
        return ResponseEntity.ok(stageService.getStagesByProjectIdFiltered(projectId, stageFilterDto));
    }

    @GetMapping("stage/{stageId}")
    public ResponseEntity<StageDto> getStageById(@PathVariable @Positive long stageId) {
        return ResponseEntity.ok(stageService.getStageDtoById(stageId));
    }

    @PostMapping
    public ResponseEntity<StageDto> createStage(@Valid @RequestBody StageDto stageDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stageService.createStage(stageDto));
    }

    @PutMapping("{stageId}/executor")
    public ResponseEntity<Void> updateStage(@PathVariable @Positive long stageId,
                                            @Valid @RequestBody TeamMemberDto teamMemberDto) {
        stageService.updateStage(stageId, teamMemberDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("{stageId}")
    public ResponseEntity<Void> deleteStage(@PathVariable @Positive long stageId) {
        stageService.deleteStage(stageId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("{stageId}/move/tasks/to/{anotherStageId}")
    public ResponseEntity<Void> deleteStageAndMoveTasks(@PathVariable @Positive long stageId,
                                                        @PathVariable @Positive long anotherStageId) {
        stageService.deleteStage(stageId, anotherStageId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
