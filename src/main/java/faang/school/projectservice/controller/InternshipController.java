package faang.school.projectservice.controller;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class InternshipController {
    private final InternshipValidator internshipValidator;
    private final InternshipService internshipService;

    @PostMapping
    public InternshipDto create(@RequestBody InternshipDto internshipDto) {
        internshipValidator.validateCreation(internshipDto);
        return internshipService.create(internshipDto);
    }


    @PutMapping
    public InternshipDto update(@RequestBody InternshipDto internshipDto) {
        internshipValidator.validateUpdating(internshipDto);
        return internshipService.update(internshipDto);
    }

    @GetMapping
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @PostMapping("/filters")
    public List<InternshipDto> getFilteredInternships(@RequestBody InternshipFilterDto filters) {
        return internshipService.getFilteredInternships(filters);
    }
}
