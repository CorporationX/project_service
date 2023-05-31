package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.dto.stage.RemoveStageDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/stages")
public class StageController {
    private final StageService stageService;

    @PostMapping
    public StageDto createStage(@RequestBody StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @GetMapping
    public List<StageDto> findStageByFilter(@RequestBody StageFilterDto stageFilterDto) {
        return stageService.findStageByFilter(stageFilterDto);
    }

    @GetMapping("/{stageId}")
    public StageDto findStageById(@PathVariable Long stageId) {
        return stageService.findStageById(stageId);
    }

    @DeleteMapping
    public void removeStageById(@RequestBody RemoveStageDto removeStageDto) {
        stageService.removeStageById(removeStageDto);
    }

    @GetMapping("/list")
    public List<StageDto> findAll() {
        return stageService.findAll();
    }

}
