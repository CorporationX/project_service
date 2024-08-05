package faang.school.projectservice.controller;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.validation.Valid;
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
@RequiredArgsConstructor
@RequestMapping("/internships")
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping("/")
    public InternshipDto create(@Valid @RequestBody InternshipDto internShipDto) {
        return internshipService.create(internShipDto);
    }

    @PutMapping("/")
    public InternshipDto update(@Valid @RequestBody InternshipDto internShipDto) {
        return internshipService.update(internShipDto);
    }

    @GetMapping("/{id}")
    public InternshipDto getInternship(@PathVariable(name = "id") Long internshipId) {
        return internshipService.getInternship(internshipId);

    }

    @GetMapping("/filtered")
    public List<InternshipDto> getFilteredInternship(@RequestBody InternshipFilterDto filters) {
        return internshipService.getFilteredInternships(filters);
    }

    @GetMapping("/all")
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

}
