package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.NewStageDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stagerole.NewStageRolesDto;
import faang.school.projectservice.model.StageDeleteMode;
import faang.school.projectservice.model.StageStatus;
import faang.school.projectservice.service.stage.StageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stages")
@RequiredArgsConstructor
@Validated
@Tag(name = "Stage controller", description = "All manipulations with stage entities are done through this controller")
public class StageController {
    private final StageService stageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "stage creation",
            description = "this method creates a new stage"
    )
    public StageDto createStage(
            @Valid
            @RequestBody
            @Parameter(description = "dto of new stage")
            NewStageDto dto) {
        return stageService.createStage(dto);
    }

    @GetMapping("/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "get stages",
            description = "this method get all stages by specific id of project it related to"
    )
    public List<StageDto> getAllStagesByProjectId(
            @Positive
            @PathVariable
            @Parameter(description = "a project id")
            long projectId) {
        return stageService.getAllStages(projectId);
    }

    @GetMapping("/project/status/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "get stages",
            description = "this method get all stages by specific id of project it related to and status of stages"
    )
    public List<StageDto> getAllStagesByProjectIdAndStatus(
            @Positive
            @PathVariable
            @Parameter(description = "a project id")
            long projectId,
            @RequestParam(name = "status")
            @Parameter(description = "a status of stage")
            StageStatus statusFilter
    ) {
        return stageService.getAllStages(projectId, statusFilter);
    }

    @DeleteMapping("/{stageId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "delete stage",
            description = "this method deletes stage by id of stage"
    )
    public void deleteStageById(
            @Positive
            @PathVariable
            @Parameter(description = "id of a stage")
            long stageId,
            @Positive @RequestHeader(name = "x-stage-id-for-tasks-to-migrate", required = false)
            @Parameter(description = "id of a stage to where tasks of deleting stage should migrate")
            long stageToMigrateId,
            @RequestParam(name = "mode")
            @Parameter(description = "indicates which delete mode should be used")
            StageDeleteMode stageDeleteMode
    ) {
        stageService.deleteStage(stageId, stageToMigrateId, stageDeleteMode);
    }

    @PutMapping("/{stageId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "update stage",
            description = "this method update roles by id of stage"
    )
    public StageDto updateStage(
            @Positive
            @PathVariable
            @Parameter(description = "a stage id")
            long stageId,
            @Valid
            @RequestBody
            @Parameter(description = "list of new roles dto")
            List<NewStageRolesDto> newStageRolesDtoList
    ) {
        return stageService.updateStage(stageId, newStageRolesDtoList);
    }

    @GetMapping(path = "/{stageId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "get stage",
            description = "this method gets stage by"
    )
    public StageDto getStageById(
            @Positive
            @PathVariable
            @Parameter(description = "a stage id")
            long stageId) {
        return stageService.getStageById(stageId);
    }
}
