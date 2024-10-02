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

    @PostMapping("/create")
    public StageCreateDto createStage(@Valid @RequestBody StageCreateDto stageCreateDto) {
        return stageService.createStage(stageCreateDto);
    }

    @PostMapping("/getFilter")
    public List<StageDto> getStagesByFilters(@RequestBody StageFilterDto stageFilterDto) {
        return stageService.getStagesByFilters(stageFilterDto);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteStage(@PathVariable long id) {
        stageService.deleteStage(id);
    }

    @PostMapping("/updateStage")
    public StageUpdateDto updateStage(@RequestBody StageUpdateDto stageUpdateDto) {
        return stageService.updateStage(stageUpdateDto);
    }

    @GetMapping("/getAllStage")
    public List<StageDto> getAllStage() {
        return stageService.getAllStage();
    }

    @GetMapping("/getStage/{id}")
    public StageDto getStageById(@PathVariable long id) {
        return stageService.getStageById(id);
    }
}
