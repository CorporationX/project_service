package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.filter.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.service.stage.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stage")
public class StageController {
    private final StageService stageService;

    @PostMapping()
    public StageDto createStage(@Valid @RequestBody StageCreateDto stageCreateDto) {
        return stageService.createStage(stageCreateDto);
    }

    @PostMapping("/filter")
    public List<StageDto> getStagesByFilters(@RequestBody StageFilterDto stageFilterDto) {
        return stageService.getStagesByFilters(stageFilterDto);
    }

    @DeleteMapping("/{id}")
    public void deleteStage(@PathVariable long id) {
        stageService.deleteStage(id);
    }

    @PatchMapping("/{id}")
    public StageDto updateStage(@PathVariable long id, @RequestBody StageUpdateDto stageUpdateDto) {
        return stageService.updateStage(id, stageUpdateDto);
    }

    @GetMapping()
    public List<StageDto> getAllStage() {
        return stageService.getAllStage();
    }

    @GetMapping("/{id}")
    public StageDto getStageById(@PathVariable long id) {
        return stageService.getStageById(id);
    }
}
