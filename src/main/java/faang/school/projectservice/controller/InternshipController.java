package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    private void validateThereIsProjectForInternship(InternshipDto internshipDto) {
        if (internshipDto.getProjectId() == null) {
            throw new DataValidationException("There is not project for create internship!");
        }
        if (internshipDto.getId() <= 0) {
            throw new DataValidationException("Id of internship can't be negative!");
        }
        if (internshipDto.getName() == null || internshipDto.getName().isBlank()) {
            throw new DataValidationException("Need create a name for the internship");
        }
    }

    public InternshipDto saveNewInternship(InternshipDto internshipDto) {
        validateThereIsProjectForInternship(internshipDto);
        return internshipService.saveNewInternship(internshipDto);
    }
}
