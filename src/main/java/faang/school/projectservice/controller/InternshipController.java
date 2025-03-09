package faang.school.projectservice.controller;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
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

    @PostMapping
    public InternshipDto createInternship(@Valid @RequestBody InternshipDto internshipDto) {
        return internshipService.create(internshipDto);
    }

    @PutMapping
    public InternshipDto updateInternship(@Valid @RequestBody InternshipUpdateDto internshipUpdateDto) {
        return internshipService.update(internshipUpdateDto);
    }

    @GetMapping("/{id}")
    public InternshipDto getInternship(@PathVariable("id") long id ) {
        return internshipService.get(id);
    }

    @GetMapping
    public List<InternshipDto> getInternshipByFilter(@RequestBody InternshipFilterDto filter) {
        return internshipService.getByFilter(filter);
    }
}
