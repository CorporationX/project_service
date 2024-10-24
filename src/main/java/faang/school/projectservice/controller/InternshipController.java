package faang.school.projectservice.controller;

import faang.school.projectservice.model.dto.InternshipFilterDto;
import faang.school.projectservice.model.dto.InternshipDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @PostMapping("/project/{projectId}")
    @Operation(summary = "Get internships by project ID", description = "Retrieve internships by project ID with optional filtering.")
    public List<InternshipDto> getInternshipsByProject(
            @Parameter(description = "ID of the project", required = true)
            @PathVariable @NotNull Long projectId,
            @Valid @RequestBody InternshipFilterDto filterDto) {
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
}
