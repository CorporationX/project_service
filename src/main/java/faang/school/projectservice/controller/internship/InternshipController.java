package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.filter.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internships")
public class InternshipController {

    private final InternshipService internshipService;

    @PostMapping
    public InternshipDto createInternship(@RequestBody InternshipDto internshipDto) {
        return internshipService.create(internshipDto);
    }

    @PutMapping
    public InternshipDto updateInternship(@RequestBody InternshipDto internshipDto) {
        return internshipService.update(internshipDto);
    }

    @GetMapping("/filters")
    public List<InternshipDto> filterInternship(@RequestBody InternshipFilterDto filters) {
        return internshipService.getFilteredInternship(filters);
    }

    @GetMapping
    public List<InternshipDto> getInternships() {
        return internshipService.getAllInternship();
    }

    @GetMapping("/{id}")
    public InternshipDto getInternship(@PathVariable("id") @Positive Long id) {
        return internshipService.getInternshipById(id);
    }
}
