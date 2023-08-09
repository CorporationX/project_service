package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internship/v1")
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;
    private final InternshipValidator internshipValidator;

    @PostMapping("/internship")
    public InternshipDto saveNewInternship(@RequestBody InternshipDto internshipDto) {
        internshipValidator.validateServiceSaveInternship(internshipDto);
        return internshipService.saveNewInternship(internshipDto);
    }

    @PutMapping("/internship/{id}")
    public InternshipDto updateInternship(@RequestBody InternshipDto internshipDto, @PathVariable Long id) {
        internshipValidator.validateControllerInternship(internshipDto);
        return internshipService.updateInternship(internshipDto, id);
    }

    @GetMapping("/internshipsWithFilter/{projectId}")
    public List<InternshipDto> findInternshipsWithFilter(@RequestBody InternshipFilterDto filterDto, @PathVariable Long projectId) {
        return internshipService.findInternshipsByStatusWithFilter(projectId, filterDto);
    }

    @GetMapping("/allInternship")
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("/allInternship/{id}")
    public InternshipDto findAllInternshipById(@PathVariable Long id) {
        return internshipService.findAllInternshipById(id);
    }
}