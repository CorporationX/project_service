package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage.StageStatus;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stage")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @PostMapping
    public StageDto createStage(@RequestBody StageDto stageDto) {
        validateStage(stageDto);
        return stageService.createStage(stageDto);
    }

    @DeleteMapping("/{stageId}")
    public void deleteStage(@PathVariable long stageId) {
        stageService.deleteStage(stageId);
    }

    @GetMapping("/{stageId}")
    public StageDto getStageById(@PathVariable long stageId) {
        return stageService.getStageById(stageId);
    }

    @GetMapping("/{projectId}")
    public List<StageDto> findAllStagesOfProject(@PathVariable Long projectId) {
        return stageService.findAllStagesOfProject(projectId);
    }

    @GetMapping("/stage/{projectId}/{status}")
    public List<StageDto> getStagesOfProjectWithFilter(@PathVariable Long projectId, @PathVariable StageStatus status) {
        return stageService.getStagesOfProjectWithFilter(projectId, status);
    }

    @PutMapping("/stage/{stageId}")
    public StageDto updateStageRoles(@PathVariable long stageId, @RequestBody StageRolesDto stageRolesDto) {
        return stageService.updateStage(stageId, stageRolesDto);
    }

    private void validateStage(StageDto stageDto) {
        if (stageDto == null) {
            throw new DataValidationException("Stage cannot be null");
        }
        if (stageDto.getProjectId() == null) {
            throw new DataValidationException("The stage NECESSARILY refers to some kind of project!!!");
        }
        if (stageDto.getStageName() == null || stageDto.getStageName().isBlank()) {
            throw new DataValidationException("Stage name cannot be null or blank");
        }
        if (stageDto.getStageRoles() == null || stageDto.getStageRoles().isEmpty()) {
            throw new DataValidationException("Stage roles cannot be null or empty");
        }
    }


}
