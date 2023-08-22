package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.ActionWithTasks;
import faang.school.projectservice.dto.stage.StageDeleteDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
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

    @GetMapping("/{stageId}")
    @ResponseStatus(HttpStatus.OK)
    public StageDto getStageById(@PathVariable Long stageId) {
        validateId(stageId);
        log.info("Getting stage: {}", stageId);
        return service.getStageById(stageId);
    }

    @DeleteMapping("/{stageId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteStageById(@PathVariable Long stageId, @RequestBody StageDeleteDto stageDeleteDto) {
        validateDeleteDto(stageDeleteDto);
        log.info("Deleting stage: {}", stageId);
        service.deleteStageById(stageDeleteDto);
    }

    @GetMapping("/get-stages-by-status/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public List<StageDto> getStagesByStatus(@PathVariable Long projectId, @RequestBody StageFilterDto stageFilterDto) {
        return service.getStagesByStatus(projectId, stageFilterDto);
    }

    private void validateDeleteDto(StageDeleteDto stageDeleteDto) {
        validateId(stageDeleteDto.getProjectId());
        validateId(stageDeleteDto.getStageId());
        validateEnum(stageDeleteDto.getAction(), ActionWithTasks.class);
    }

    private <T> void validateEnum(T obj, Class<? extends Enum> enumClass) {
        if (!enumClass.isInstance(obj)) {
            throw new DataValidationException("Invalid enum");
        }
    }

    private void validateStage(StageDto stageDto) {
        validateId(stageDto.getStageId());
        validateId(stageDto.getProjectId());
        validateList(stageDto.getStageRoleIds());
        validateList(stageDto.getTeamMemberIds());
        if (stageDto.getStageName() == null || stageDto.getStageName().isEmpty()) {
            throw new DataValidationException("Stage name must not be empty");
        }
    }

    private void validateList(List<?> list) {
        if (list == null || list.isEmpty()) {
            throw new DataValidationException("List must not be empty");
        }
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new DataValidationException("Id must not be null");
        }
    }
}
