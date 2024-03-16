package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.service.stage.StageService;
import faang.school.projectservice.validator.StageValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stage")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;
    private final StageValidator stageValidator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageDto create (@Valid @RequestBody StageDto stageDto) {
        stageValidator.validateName(stageDto);

        return stageService.create(stageDto);
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public StageDto update (
            @PathVariable("id") Long stageId,
            @Valid @RequestBody StageRolesDto stageRolesDto) {
        return stageService.update(stageId, stageRolesDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public StageDto remove (@PathVariable("id") Long stageId) {
        stageValidator.validateStageExistence(stageId);

        return stageService.remove(stageId);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public StageDto get(@PathVariable("id") Long stageId) {
        stageValidator.validateStageExistence(stageId);

        return stageService.getStage(stageId);
    }

    @GetMapping("/project/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<StageDto> getAllStages(@PathVariable("id") Long projectId) {
        return stageService.getAllStages(projectId);
    }

    @GetMapping("/project/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<StageDto> getStagesFiltered(
            @PathVariable("id") Long projectId,
            @Valid @RequestBody StageFilterDto stageFilterDto) {
        return stageService.getStagesFiltered(projectId, stageFilterDto);
    }
}
