package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.TeamRoleTaskStatusDto;
import faang.school.projectservice.service.stage.StageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/stages")
@RequiredArgsConstructor
@Tag(name = "StageController", description = "Provides several operations, related to stages")
public class StageController {
    private final StageService stageService;

    @GetMapping("/{id}")
    @Operation(summary = "Getting stage", description = "Provides ability to find stage by id")
    @ApiResponse(responseCode = "404", description = "Stage doesn't exist")
    public StageDto findById(@PathVariable @Parameter(description = "Stage id", required = true) long id) {
        return stageService.findById(id);
    }

    @GetMapping("/project/{id}")
    @Operation(summary = "Getting all stages for project", description = "Provides ability to find all stages by project id")
    @ApiResponse(responseCode = "404", description = "Stage doesn't exist")
    public List<StageDto> findAll(@PathVariable @Parameter(description = "Stage id", required = true) long id) {
        return stageService.findAllStages(id);
    }

    @PutMapping
    @Operation(summary = "Updating stage", description = "Provides ability to update stage")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Stage doesn't exist"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public void update(@RequestBody @Valid @Parameter(description = "Stage to update", required = true) StageDto stageDto) {
        stageService.updateStage(stageDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleting stage", description = "Provides ability to delete stage by id")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Stage doesn't exist"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public void deleteStageOfProject(@PathVariable @Parameter(description = "Stage to delete id", required = true) long id,
                                     @RequestBody @Valid @Parameter(description = "Stage to delete", required = true) StageDto stageDto) {
        stageService.deleteStage(id, stageDto);
    }

    @PostMapping
    @Operation(summary = "Creating stage", description = "Provides ability to create new stage")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public void saveStage(@RequestBody @Valid @Parameter(description = "Stage to create", required = true) StageDto stageDto) {
        stageService.save(stageDto);
    }

    @GetMapping("/filter")
    @Operation(summary = "Find filtered stages", description = "Provides ability to find stages with filter")
    public List<StageDto> findAllWithFilter(@RequestBody TeamRoleTaskStatusDto teamRoleTaskStatusDTO) {
        return stageService.getStagesWithFilters(teamRoleTaskStatusDTO.getTeamRole(), teamRoleTaskStatusDTO.getTaskStatus());
    }

}
