package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.stage.DeleteStageDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @DeleteMapping("/{stageId1}")
    public void deleteStage(@PathVariable Long stageId1, @RequestBody DeleteStageDto deleteStageDto, @RequestParam("id2") Long stageId2) {
        stageService.deleteStage(stageId1, deleteStageDto, stageId2);
    }

    @GetMapping
    public List<StageDto> getAllStage(ProjectDto projectDto) {
        return stageService.getAllStage(projectDto);
    }

    @GetMapping("/{id}")
    public StageDto getStageById(@PathVariable Long id) {
        return stageService.getStageById(id);
    }

    @PostMapping
    public List<StageDto> filterByStatus(@RequestBody StageFilterDto stageFilterDto) {
        return stageService.filterByStatus(stageFilterDto);
    }

    @PutMapping("/{id}")
    public void updateStageRoles(@PathVariable Long id, @RequestBody StageRolesDto stageRoles) {
        stageService.updateStageRoles(id, stageRoles);
    }
}