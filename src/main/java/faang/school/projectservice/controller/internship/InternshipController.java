package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.internship.InternshipService;
import faang.school.projectservice.validator.internship.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService service;
    private final InternshipValidator validator;

    @PostMapping
    public InternshipDto createInternship(@RequestBody InternshipDto internshipDto) {
        validator.internshipControllerValidation(internshipDto);
        return service.createInternship(internshipDto);
    }

    @PutMapping
    public InternshipDto updateInternship(@RequestParam long id, @RequestBody InternshipDto internshipDto) {
        if (id < 1) {
            throw new DataValidationException("ID error!");
        }
        validator.internshipControllerValidation(internshipDto);
        return service.updateInternship(id, internshipDto);
    }

    @GetMapping
    public InternshipDto findInternshipById(@RequestParam long id) {
        if (id < 1) {
            throw new DataValidationException("ID error!");
        }
        return service.findInternshipById(id);
    }

    @GetMapping
    public List<InternshipDto> findAllInternships() {
        return service.findAllInternships();
    }

    @GetMapping
    public List<InternshipDto> findInternshipsWithFilter(@RequestParam long projectId, @RequestBody InternshipFilterDto filterDto) {
        return service.findInternshipsWithFilter(projectId, filterDto);
    }


}
