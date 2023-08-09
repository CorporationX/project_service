package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internship/v1")
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;
    private final InternshipValidator internshipValidator;

    @PostMapping("/internship")
    public InternshipDto saveNewInternship(@RequestBody InternshipDto internshipDto) {
        internshipValidator.validateControllerInternship(internshipDto);
        return internshipService.saveNewInternship(internshipDto);
    }

    @PutMapping("/internship/{id}")
    public InternshipDto updateInternship(@RequestBody InternshipDto internshipDto,@PathVariable long id) {
        internshipValidator.validateControllerInternship(internshipDto);
        return internshipService.updateInternship(internshipDto, id);
    }
}
