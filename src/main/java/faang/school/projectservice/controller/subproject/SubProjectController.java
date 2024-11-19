package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/sub-projects")
@Validated
public class SubProjectController {
    private final SubProjectService subProjectService;
    private final UserContext userContext;

    @PostMapping("/project/{parentProjectId}")
    @Operation(summary = "Create sub project in DB")
    public CreateSubProjectDto createSubProject(@PathVariable @Positive @NotNull Long parentProjectId,
                                                @RequestBody @Valid CreateSubProjectDto subProjectDto) {
        return subProjectService.createSubProject(parentProjectId, subProjectDto);
    }

    @PutMapping("/project/{projectId}")
    @Operation(summary = "Update project by id",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true, description = "User id")
    )
    public CreateSubProjectDto updateProject(@PathVariable @Positive @NotNull Long projectId,
                                             @RequestBody @Valid CreateSubProjectDto subProjectDto) {
        long userId = userContext.getUserId();
        return subProjectService.updateProject(projectId, subProjectDto, userId);
    }

    @GetMapping("/project/{projectId}/projects")
    @Operation(summary = "Get sub projects by parent project id and filters")
    public List<CreateSubProjectDto> getProjectsByFilters(@PathVariable @Positive @NotNull Long projectId,
                                                          @RequestBody @Valid FilterSubProjectDto filterDto
    ) {
        return subProjectService.getProjectsByFilter(projectId, filterDto);
    }
}

