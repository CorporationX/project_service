package faang.school.projectservice.controller;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.*;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    private void validateInternship(Internship internship) {
        if (internship == null || internship.getId() == null) {
            throw new DataValidationException("There is not internship!");
        }
    }
    public Internship updateInternship(Internship internship, List<TeamMember> interns, Task task) {
        validateInternship(internship);
        return internshipService.updateInternship(internship, interns, task);
    }
}
