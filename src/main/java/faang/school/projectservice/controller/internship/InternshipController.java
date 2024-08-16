package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internship")

public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public InternshipDto create(@Valid @RequestBody InternshipDto internshipDto) {
        return internshipService.createInternship(internshipDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InternshipDto updateInternship(@PathVariable long id, @Valid @RequestBody InternshipDto internshipDto) {
        return internshipService.updateInternship(id, internshipDto);
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public List<InternshipDto> getInternshipByFilter(@RequestBody InternshipFilterDto internshipFilterDto) {
        return internshipService.getInternshipByFilter(internshipFilterDto);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InternshipDto getInternshipById(@PathVariable long id) {
        return internshipService.getInternshipById(id);
    }
}
