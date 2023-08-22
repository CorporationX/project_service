package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.stage.StageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stage")
@Slf4j
public class StageController {

    private final StageService service;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public StageDto create(@RequestBody StageDto stageDto) {
        validateStage(stageDto);
        log.info("Creating stage: {}", stageDto);
        return service.create(stageDto);
    }

    @GetMapping("/{projectId}/stages")
    @ResponseStatus(HttpStatus.OK)
    public List<StageDto> getStages(@PathVariable Long projectId) {
        validateId(projectId);
        log.info("Getting stages for project: {}", projectId);
        return service.getStagesByProjectId(projectId);
    }

    private void validateStage(StageDto stageDto) {
        validateId(stageDto.getStageId());
        validateId(stageDto.getProjectId());
        if (stageDto.getStageName() == null || stageDto.getStageName().isEmpty()) {
            throw new DataValidationException("Stage name must not be empty");
        }
        if (stageDto.getStageRoleIds() == null || stageDto.getStageRoleIds().isEmpty()) {
            throw new DataValidationException("Stage must have at least one role");
        }
        if (stageDto.getTeamMemberIds() == null || stageDto.getTeamMemberIds().isEmpty()) {
            throw new DataValidationException("Stage must have at least one team member");
        }
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new DataValidationException("Id must not be null");
        }
    }
}
