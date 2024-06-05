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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
@RequestMapping(path = "/stage")
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "get stages",
            description = "this method get all stages by specific id of project it related to"
    )
    public List<StageDto> getAllStagesByProjectId(
            @PositiveOrZero
            @NotNull
            @RequestHeader(name = "x-project-id")
            @Parameter(description = "a project id")
            Long projectId) {
        return stageService.getAllStages(projectId);
    }

    @GetMapping(path = "/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "get stages",
            description = "this method get all stages by specific id of project it related to and status of stages"
    )
    public List<StageDto> getAllStagesByProjectIdAndStatus(
            @PositiveOrZero
            @NotNull
            @RequestHeader(name = "x-project-id")
            @Parameter(description = "a project id") Long projectId,
            @RequestParam(name = "status")
            @Parameter(description = "a status of stage")
            StageStatus statusFilter
    ) {
        return stageService.getAllStages(projectId, statusFilter);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "delete stage",
            description = "this method deletes stage by id of stage"
    )
    public void deleteStageById(
            @PositiveOrZero
            @NotNull
            @PathVariable(name = "id")
            @Parameter(description = "id of a stage")
            Long stageId,
            @PositiveOrZero @RequestHeader(name = "x-stage-id-for-tasks-to-migrate", required = false)
            @Parameter(description = "id of a stage to where tasks of deleting stage should migrate")
            Long stageToMigrateId,
            @RequestParam(name = "mode")
            @Parameter(description = "indicates which delete mode should be used")
            StageDeleteMode stageDeleteMode
    ) {
        stageService.deleteStage(stageId, stageToMigrateId, stageDeleteMode);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "update stage",
            description = "this method update roles by id of stage"
    )
    public StageDto updateStage(
            @Positive
            @NotNull
            @PathVariable(name = "id")
            @Parameter(description = "a stage id")
            Long stageId,
            @Valid
            @RequestBody
            @Parameter(description = "list of new roles dto")
            List<NewStageRolesDto> newStageRolesDtoList
    ) {
        return stageService.updateStage(stageId, newStageRolesDtoList);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "get stage",
            description = "this method gets stage by"
    )
    public StageDto getStageById(
            @PositiveOrZero
            @NotNull
            @PathVariable(name = "id")
            @Parameter(description = "a stage id")
            Long stageId) {
        return stageService.getStageById(stageId);
    }
}
