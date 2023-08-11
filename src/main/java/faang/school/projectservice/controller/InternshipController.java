package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/internship")
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;
    private final InternshipValidator internshipValidator;

    @PostMapping("/")
    public InternshipDto saveNewInternship(@RequestBody InternshipDto internshipDto) {
        internshipValidator.validateControllerInternship(internshipDto);
        return internshipService.saveNewInternship(internshipDto);
    }

    @PutMapping("/{id}")
    public InternshipDto updateInternship(@RequestBody InternshipDto internshipDto, @PathVariable Long id) {
        internshipValidator.validateControllerInternship(internshipDto);
        return internshipService.updateInternship(internshipDto, id);
    }
}
