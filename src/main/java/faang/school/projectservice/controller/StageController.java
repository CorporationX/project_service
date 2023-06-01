package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.dto.stage.ForwardStageDto;
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

    @PostMapping("/find")
    public List<StageDto> findStageByFilter(@RequestBody StageFilterDto stageFilterDto) {
        return stageService.findStageByFilter(stageFilterDto);
    }

    @GetMapping("/{stageId}")
    public StageDto findStageById(@PathVariable Long stageId) {
        return stageService.findStageById(stageId);
    }

    @DeleteMapping("/{stageId}")
    public void removeStageById(@PathVariable Long stageId) {
        stageService.removeStageById(stageId);
    }

    @GetMapping("/close/{stageId}")
    public void closeStageById(@PathVariable Long stageId) {
        stageService.closeStageById(stageId);
    }

    @PostMapping("/forward")
    public void forwardStageById(@RequestBody ForwardStageDto forwardStageDto) {
        stageService.forwardStage(forwardStageDto);
    }

    @GetMapping("/list")
    public List<StageDto> findAll() {
        return stageService.findAll();
    }

}
