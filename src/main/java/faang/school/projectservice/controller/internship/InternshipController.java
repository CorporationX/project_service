package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;
    private final InternshipControllerValidation internshipControllerValidation;

    public InternshipDto create(InternshipDto internshipDto) {
        internshipControllerValidation.validationDto(internshipDto);

        return internshipService.create(internshipDto);
    }
}
