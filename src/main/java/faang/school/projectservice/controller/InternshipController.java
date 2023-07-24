package faang.school.projectservice.controller;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    private void validateThereIsProjectForInternship(Internship internship) {
        if (internship.getProject() == null) {
            throw new DataValidationException("There is not project for create internship!");
        }
        if (internship.getId() <= 0) {
            throw new DataValidationException("Id of internship can't be negative!");
        }
        if (internship.getName() == null) {
            throw new DataValidationException("Need create a name for the internship");
        }
    }

    public Internship saveNewInternship(Internship internship) {
        validateThereIsProjectForInternship(internship);
        return internshipService.saveNewInternship(internship);
    }
}
