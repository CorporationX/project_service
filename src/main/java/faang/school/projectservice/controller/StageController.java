package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MethodDeletingStageDto;
import faang.school.projectservice.dto.ProjectStatusFilterDto;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.StageService;
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

    @PostMapping
    public StageDto createStage(@RequestBody StageDto stageDto) {
        validateStage(stageDto);
        return stageService.createStage(stageDto);
    }

    @GetMapping("/filter")
    public List<StageDto> getStagesByProjectStatus(@RequestBody ProjectStatusFilterDto filter) {
        validateProjectStatusFilterDto(filter);
        return stageService.getStagesByProjectStatus(filter);
    }

    @DeleteMapping("/{id}")
    public void deleteStage(@PathVariable Long id, @RequestBody MethodDeletingStageDto methodDeletingStageDto,
                            @RequestParam("stageId") @Nullable Long stageId) {
        validationId(id);
        stageService.deleteStage(id, methodDeletingStageDto, stageId);
    }

    @PutMapping("/{id}")
    public void updateStageRoles(@PathVariable Long id, @RequestBody StageRolesDto stageRoles) {
        validationId(id);
        stageService.updateStageRoles(id, stageRoles);
    }

    private void validationId(Long id) {
        if (id == null) {
            throw new DataValidationException("Id cannot be null");
        }
    }

    private void validateProjectStatusFilterDto(ProjectStatusFilterDto filter) {
        if (filter.getStatus() == null) {
            throw new DataValidationException("Status cannot be null");
        }
    }

    private void validateStage(StageDto stageDto) {
        if (stageDto == null) {
            throw new DataValidationException("Stage cannot be null");
        }
        if (stageDto.getProject() == null) {
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
