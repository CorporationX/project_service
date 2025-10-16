package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.dto.stage.StageViewDto;
import faang.school.projectservice.model.stage.enums.DeleteStrategy;
import faang.school.projectservice.service.stage.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST контроллер для управления этапами проекта.
 * <p>
 * Обеспечивает полный цикл управления этапами проекта через REST API.
 * </p>
 *
 * @author bozya
 * @since 31.07.2025
 */
@RestController
@RequestMapping("/{projectId}/stages")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @PostMapping
    public ResponseEntity<StageViewDto> create(
            @RequestBody @Valid StageCreateDto stageCreateDto) {
        StageViewDto createdStage = stageService.create(stageCreateDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdStage);
    }

    @GetMapping("/{stageId}")
    public ResponseEntity<StageViewDto> getById(@PathVariable Long stageId) {
        StageViewDto stage = stageService.getById(stageId);
        return ResponseEntity.ok(stage);
    }

    @GetMapping
    public ResponseEntity<List<StageViewDto>> getAll(
            @PathVariable Long projectId,
            @ModelAttribute StageFilterDto filters) {
        List<StageViewDto> stages = stageService.getAllStagesWithFilters(filters);
        return ResponseEntity.ok(stages);
    }

    @PutMapping("/{stageId}")
    public ResponseEntity<StageViewDto> update(
            @PathVariable Long stageId,
            @RequestBody @Valid StageUpdateDto updateDto) {
        StageViewDto updatedStage = stageService.update(updateDto, stageId);
        return ResponseEntity.ok(updatedStage);
    }

    @DeleteMapping("/{stageId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long stageId,
            @RequestParam DeleteStrategy strategy) {
        stageService.delete(stageId, strategy);
        return ResponseEntity.noContent().build();
    }
}