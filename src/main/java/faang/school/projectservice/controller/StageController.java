package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.stage.DeleteStageDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @PostMapping
    public StageDto createStage(@RequestBody StageDto stage) {
        return stageService.createStage(stage);
    }

    @DeleteMapping("{id1}")
    public void deleteStage(@PathVariable Long id1, @RequestBody DeleteStageDto deleteStageDto, @RequestParam("id2") Long id2) {
        stageService.deleteStage(id1, deleteStageDto, id2);
    }

    @PutMapping
    public List<StageDto> getAllStage(@RequestBody ProjectDto projectDto) {
        return stageService.getAllStage(projectDto);
    }

    @PutMapping
    public StageDto getStageById(@RequestBody Long id) {
        return stageService.getStageById(id);
    }

    @PutMapping
    public List<StageDto> filterByStatus(@RequestBody StageFilterDto stageFilterDto) {
        return stageService.filterByStatus(stageFilterDto);
    }
}
