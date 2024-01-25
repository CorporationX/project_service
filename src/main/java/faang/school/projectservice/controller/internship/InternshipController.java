package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        if (internshipDto == null || internshipDto.getId() == null || internshipDto.getId() < 0)
            throw new IllegalArgumentException("Internship cannot be created");
        return internshipService.createInternship(internshipDto);
    }
}
