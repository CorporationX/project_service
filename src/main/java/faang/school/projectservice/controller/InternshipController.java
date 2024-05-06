package faang.school.projectservice.controller;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.dto.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validation.InternshipValidator;
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
@RequestMapping("/internship")
public class InternshipController {

    private final InternshipService internshipService;
    private final InternshipValidator internshipValidator;

    @PostMapping
    public InternshipDto create(@RequestBody InternshipDto internshipDto) {
        internshipValidator.validateInternshipDto(internshipDto);
        return internshipService.create(internshipDto);
    }

    @PutMapping("/{id}")
    public InternshipDto update(@RequestBody InternshipDto internshipDto, @PathVariable long id) {
        internshipValidator.validateInternshipDto(internshipDto);
        return internshipService.update(internshipDto, id);
    }

    @GetMapping("/filter")
    public List<InternshipDto> findAllWithFilter(@RequestBody InternshipFilterDto internshipFilterDto) {
        return internshipService.findAllWithFilter(internshipFilterDto);
    }

    @GetMapping
    public List<InternshipDto> findAll() {
        return internshipService.findAll();
    }

    @GetMapping("/{id}")
    public InternshipDto findById(@PathVariable long id) {
        return internshipService.findById(id);
    }
}
