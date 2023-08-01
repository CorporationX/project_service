package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping("/saveInternship")
    public InternshipDto saveNewInternship(@RequestBody InternshipDto internshipDto) {
        InternshipValidator.validateControllerInternship(internshipDto);
        return internshipService.saveNewInternship(internshipDto);
    }
}
