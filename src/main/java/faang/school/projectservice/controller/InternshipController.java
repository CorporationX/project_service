package faang.school.projectservice.controller;

import faang.school.projectservice.dto.internship.InternshipCreateDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/internship")
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping()
    public InternshipCreateDto createInternship(@Valid @RequestBody InternshipCreateDto internshipCreateDto) {
        return internshipService.create(internshipCreateDto);
    }

    @PutMapping()
    public InternshipCreateDto updateInternship( @Valid @RequestBody InternshipUpdateDto internshipUpdateDto) {
        return internshipService.updateInternship(internshipUpdateDto);
    }

    @GetMapping("/internship-by-id/{id}")
    public InternshipCreateDto getInternship(@PathVariable("id") long id ) {
        return internshipService.getInternship(id);
    }

    @GetMapping("/internship-by-filter")
    public List<InternshipCreateDto> getInternshipByFilter(@RequestBody InternshipFilterDto filter) {
        return internshipService.getInternshipByFilter(filter);
    }
}
