package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping("/saveInternship")
    public InternshipDto saveNewInternship(@RequestBody InternshipDto internshipDto) {
        InternshipValidator.validateControllerInternship(internshipDto);
        return internshipService.saveNewInternship(internshipDto);
    }

    @PutMapping("/updateInternship")
    public InternshipDto updateInternship(@RequestBody InternshipDto internshipDto, long id) {
        InternshipValidator.validateControllerInternship(internshipDto);
        return internshipService.updateInternship(internshipDto, id);
    }

    @GetMapping("/findInternshipsWithFilter")
    public List<InternshipDto> findInternshipsWithFilter(long projectId, @RequestBody InternshipFilterDto filterDto) {
        return internshipService.findInternshipsByStatusWithFilter(projectId, filterDto);
    }

    @GetMapping("/getAllInternship")
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("/findAllInternshipById/{id}")
    public InternshipDto findAllInternshipById(@PathVariable long id) {
        return internshipService.findAllInternshipById(id);
    }
}