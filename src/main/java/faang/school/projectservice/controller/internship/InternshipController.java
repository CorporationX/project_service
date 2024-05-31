package faang.school.projectservice.controller.internship;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipToCreateDto;
import faang.school.projectservice.dto.internship.InternshipToUpdateDto;
import faang.school.projectservice.service.internship.InternshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internships")
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;
    private final UserContext userContext;

    @Operation(summary = "Create a new internship")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Internship created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternshipDto.class))})
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InternshipDto createInternship(@RequestBody @Valid InternshipToCreateDto internshipDto) {
        long userId = userContext.getUserId();
        return internshipService.createInternship(userId, internshipDto);
    }

    @Operation(summary = "Update an internship")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Internship updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternshipDto.class))})
    })
    @PutMapping("/{internshipId}")
    @ResponseStatus(HttpStatus.OK)
    public InternshipDto updateInternship(@PathVariable("internshipId") long internshipId,
                                          @RequestBody InternshipToUpdateDto internshipDto) {
        long userId = userContext.getUserId();
        return internshipService.updateInternship(userId, internshipId, internshipDto);
    }

    @Operation(summary = "Get internships by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of internships",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternshipDto.class))})
    })
    @GetMapping("/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public List<InternshipDto> getAllProjectInternships(
            @PathVariable("projectId") long projectId,
            @ParameterObject @RequestBody(required = false) InternshipFilterDto filter) {
        return internshipService.getAllInternshipsByProjectId(projectId, filter);
    }

    @Operation(summary = "Get all internships")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all internships",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternshipDto.class))})
    })
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<InternshipDto> getAllInternships(
            @ParameterObject @RequestBody(required = false) InternshipFilterDto filter) {
        return internshipService.getAllInternships(filter);
    }

    @Operation(summary = "Get internship by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Internship details",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternshipDto.class))})
    })
    @GetMapping("/{internshipId}")
    @ResponseStatus(HttpStatus.OK)
    public InternshipDto getInternshipById(@PathVariable("internshipId") long internshipId) {
        return internshipService.getInternshipById(internshipId);
    }
}