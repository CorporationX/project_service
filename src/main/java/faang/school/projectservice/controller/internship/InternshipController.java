package faang.school.projectservice.controller.internship;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public InternshipDto createInternship(@RequestBody @Valid InternshipDto internshipDto) {
        long userId = userContext.getUserId();
        System.out.println("USER ID:" + userId);
        return internshipService.createInternship(userId, internshipDto);
    }

    @Operation(summary = "Add a new intern to an internship")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intern added to internship",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternshipDto.class))})
    })
    @PutMapping("/{internshipId}/add/intern/{internId}")
    @ResponseStatus(HttpStatus.OK)
    public InternshipDto addNewIntern(
            @PathVariable("internshipId") long internshipId,
            @PathVariable("internId") long internId) {
        return internshipService.addNewIntern(internshipId, internId);
    }

    @Operation(summary = "Finish an internship for an intern with a specified role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Internship finished for intern",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternshipDto.class))})
    })
    @PutMapping("/{internshipId}/finish/intern/{internId}")
    @ResponseStatus(HttpStatus.OK)
    public InternshipDto finishInternshipForIntern(
            @PathVariable("internshipId") long internshipId,
            @PathVariable("internId") long internId,
            @RequestParam String role) {
        return internshipService.finishInternshipForIntern(internshipId, internId, role);
    }

    @Operation(summary = "Remove an intern from an internship")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intern removed from internship",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternshipDto.class))})
    })
    @DeleteMapping("/{internshipId}/intern/{internId}")
    @ResponseStatus(HttpStatus.OK)
    public InternshipDto removeInternFromInternship(
            @PathVariable("internshipId") long internshipId,
            @PathVariable("internId") long internId) {
        return internshipService.removeInternFromInternship(internshipId, internId);
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
                                          @RequestBody InternshipDto internshipDto) {
        return internshipService.updateInternship(internshipId, internshipDto);
    }

    @Operation(summary = "Get internships by filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of internships",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternshipDto.class))})
    })
    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public List<InternshipDto> getInternshipsByFilter(
            @ParameterObject @RequestBody InternshipFilterDto filters) {
        return internshipService.getInternshipsByFilter(filters);
    }

    @Operation(summary = "Get all internships")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all internships",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InternshipDto.class))})
    })
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
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