package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.service.StageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Этапы проекта")
@RestController
@RequestMapping("${spring.servlet.mvc.path}/stages")
@Validated
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @Operation(description = "Создать этап")
    @PostMapping
    public ResponseEntity<StageDto> createStage(@Valid @RequestBody StageDto stageDto) {
        StageDto retStage = stageService.createStage(stageDto);
        return ResponseEntity.ok(retStage);
    }

    @Operation(description = "Обновить этап")
    @PutMapping
    public ResponseEntity<StageDto> updateStage(@Valid @RequestBody StageDto stageDto) {
        StageDto retStage = stageService.updateStage(stageDto);
        return ResponseEntity.ok(retStage);
    }

    @Operation(description = "Получить все этапы по фильтру")
    @PostMapping("/filter")
    public ResponseEntity<List<StageDto>> getAllStagesByFilter(@RequestBody StageFilterDto filter) {
        return ResponseEntity.ok( stageService.getAllStagesByFilter(filter));
    }

    @Operation(description = "Получить все этапы")
    @GetMapping
    public ResponseEntity<List<StageDto>> getAllStages() {
        List<StageDto> retStage = stageService.getAllStages();
        return ResponseEntity.ok(retStage);
    }

    @Operation(description = "Удалить этап по ID")
    @DeleteMapping("/{stageId}")
    public ResponseEntity<Void> deleteStage(@PathVariable long stageId) {
        stageService.deleteStage(stageId);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Получить этап по ID")
    @GetMapping("/{stageId}")
    public ResponseEntity<StageDto> getStageById(@PathVariable long stageId) {
        StageDto retStage = stageService.getStageById(stageId);
        return ResponseEntity.ok(retStage);
    }
}
