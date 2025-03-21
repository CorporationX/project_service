package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/internships")
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping()
    public InternshipDto createInternship(@RequestBody InternshipDto internshipDto) {
        return internshipService.createInternship(internshipDto);
    }

    @GetMapping("/{id}")
    public InternshipDto getInternshipById(@PathVariable Long id) {
        return internshipService.getInternshipById(id);
    }

    @PutMapping("/{id}")
    public InternshipDto updateInternship(@RequestBody InternshipDto internshipDto, @PathVariable Long id) {
        return internshipService.updateInternship(internshipDto, id);
    }

    @GetMapping()
    public List<InternshipDto> findAllInternships() {
        return internshipService.getAllInternships();
    }

    @PostMapping("/filter")
    public List<InternshipDto> findInternshipsByFilter(@RequestBody InternshipFilterDto internshipFilterDto) {
        return internshipService.getInternshipsFiltered(internshipFilterDto);
    }
}
