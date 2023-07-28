package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.dto.SubtaskActionDto;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.validator.StageValidator;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
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

    private final StageValidator stageValidator;

    @PostMapping
    public StageDto createStage(@RequestBody StageDto stageDto) {
        stageValidator.validateStage(stageDto);
        return stageService.createStage(stageDto);
    }

    @DeleteMapping("/{oldStageId}")
    public void deleteStage(@PathVariable long oldStageId,
                            @RequestBody SubtaskActionDto subtaskActionDto,
                            @RequestParam("newStageId") @Nullable Long newStageId) {
        stageService.deleteStage(oldStageId, subtaskActionDto, newStageId);
    }

    @PutMapping("/roles/{id}")
    public StageRolesDto updateStageRoles(@PathVariable long id, @RequestBody StageRolesDto stageRoles) {
        return stageService.updateStageRoles(id, stageRoles);
    }

    @GetMapping
    public List<StageDto> getAllStages() {
        return stageService.getAllStages();
    }

    @GetMapping("/{id}")
    public StageDto getStageById(@PathVariable long id) {
        return stageService.getStageById(id);
    }
}
