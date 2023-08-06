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

    @PutMapping("/updateInternship/{id}")
    public InternshipDto updateInternship(@RequestBody InternshipDto internshipDto,@PathVariable Long id) {
        InternshipValidator.validateControllerInternship(internshipDto);
        return internshipService.updateInternship(internshipDto, id);
    }

    @GetMapping("/findInternshipsWithFilter/{projectId}")
    public List<InternshipDto> findInternshipsWithFilter(@RequestBody InternshipFilterDto filterDto,@PathVariable Long projectId) {
        return internshipService.findInternshipsByStatusWithFilter(projectId, filterDto);
    }

    @GetMapping("/getAllInternship")
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("/findAllInternshipById/{id}")
    public InternshipDto findAllInternshipById(@PathVariable Long id) {
        return internshipService.findAllInternshipById(id);
    }
}