package faang.school.projectservice.controller;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.dto.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
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
@Tag(name = "Internship", description = "The Internship API")
public class InternshipController {

    private final InternshipService internshipService;
    private static final String ID_ERR_MESSAGE = "Internship id must be more then 0";

    @Operation(summary = "Create a Internship", tags = "Internship")
    @PostMapping
    public InternshipDto create(@Valid @RequestBody InternshipDto internshipDto) {
        return internshipService.create(internshipDto);
    }

    @Operation(summary = "Update Internship by id", tags = "Internship")
    @PutMapping("/{id}")
    public InternshipDto update(@Valid @RequestBody InternshipDto internshipDto,
                                @PathVariable @Min(value = 1, message = ID_ERR_MESSAGE) long id) {
        return internshipService.update(internshipDto, id);
    }

    @Operation(summary = "Get filtered Internships. Filter conditions are passed in the request body.", tags = "Internship")
    @GetMapping("/filter")
    public List<InternshipDto> findAllWithFilter(@Valid @RequestBody InternshipFilterDto internshipFilterDto) {
        return internshipService.findAllWithFilter(internshipFilterDto);
    }

    @Operation(summary = "Gets all Internship", tags = "Internship")
    @GetMapping
    public List<InternshipDto> findAll() {
        return internshipService.findAll();
    }

    @Operation(summary = "Find an Internship by id", tags = "Internship")
    @GetMapping("/{id}")
    public InternshipDto findById(@PathVariable @Min(value = 1, message = ID_ERR_MESSAGE) long id) {
        return internshipService.findById(id);
    }
}
