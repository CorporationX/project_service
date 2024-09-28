package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.InternshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internships")
@Tag(name = "Internship Management", description = "API for managing internships")
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping()
    @Operation(summary = "Create an internship", description = "Create a new internship.")
    public InternshipDto create(@Valid @RequestBody InternshipDto internshipDto) {
        return internshipService.create(internshipDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an internship", description = "Update an existing internship by its ID.")
    public InternshipDto update(
            @Parameter(description = "ID of the internship to update", required = true)
            @PathVariable @NotNull long id, @Valid @RequestBody InternshipDto internshipDto) {
        return internshipService.update(id, internshipDto);
    }
//TODO переделай на POST
    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get internships by project ID", description = "Retrieve internships by project ID with optional filtering.")
    public List<InternshipDto> getInternshipsByProject(
            @Parameter(description = "ID of the project", required = true)
            @PathVariable @NotNull Long projectId,
            @RequestParam(required = false) InternshipStatus statusPattern,
            @RequestParam(required = false) TeamRole rolePattern) {

        InternshipFilterDto filterDto = new InternshipFilterDto();
        filterDto.setStatusPattern(statusPattern);
        filterDto.setRolePattern(rolePattern);
        return internshipService.getInternshipsByProjectAndFilter(projectId, filterDto);
    }

    @GetMapping
    @Operation(summary = "Get all internships", description = "Retrieve all internships with pagination.")
    public Page<InternshipDto> getAllInternships(
            @PageableDefault(size = 20, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return internshipService.getAllInternships(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an internship by ID", description = "Retrieve an internship by its ID.")
    public InternshipDto getInternshipById(
            @Parameter(description = "ID of the internship", required = true)
            @PathVariable Long id) {
        return internshipService.getInternshipById(id);
    }
//TODO не забудь что появился global exception handler
    @ExceptionHandler(DataValidationException.class)
    public String handleDataValidationException(DataValidationException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException ex) {
        return ex.getMessage();
    }

}
