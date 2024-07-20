package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validator.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;
    private final Validator validator;

    public InternshipDto create(InternshipDto internshipDto) {
        validator.checkInternshipDto(internshipDto);
        return internshipService.create(internshipDto);
    }

    public InternshipDto updateEvent(InternshipDto internshipDto) {
        validator.checkInternshipDto(internshipDto);
        return internshipService.updateEvent(internshipDto);
    }

}
