package faang.school.projectservice.controller;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.dto.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validator.InternshipValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/internship")
@Validated
@Tag(name = "Internship", description = "Operations related to internship")
public class InternshipController {

    private final InternshipService internshipService;
    private final InternshipValidator internshipValidator;
    private static final String ID_ERR_MESSAGE = "Internship id must be more then 0";

    @PostMapping
    @Operation(summary = "Create a new internship", description = "Create a new internship")
    public InternshipDto create(@Valid @RequestBody InternshipDto internshipDto) {
        return internshipService.create(internshipDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an internship", description = "Updates an existing internship")
    public InternshipDto update(@Valid @RequestBody InternshipDto internshipDto,
                                @PathVariable @Min(value = 1, message = ID_ERR_MESSAGE) long id) {
        return internshipService.update(internshipDto, id);
    }

    @GetMapping("/filter")
    @Operation(summary = "Get all internships with filter", description = "Retrieves a list of filtered internships")
    public List<InternshipDto> findAllWithFilter(@Valid @RequestBody InternshipFilterDto internshipFilterDto) {
        return internshipService.findAllWithFilter(internshipFilterDto);
    }

    @GetMapping
    @Operation(summary = "Get all internships", description = "Retrieves a list of all internships")
    public List<InternshipDto> findAll() {
        return internshipService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get internship by ID", description = "Retrieves an internship by its ID")
    public InternshipDto findById(@PathVariable @Min(value = 1, message = ID_ERR_MESSAGE) long id) {
        return internshipService.findById(id);
    }
}