package faang.school.projectservice.controller;

import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    private void validateThereIsProjectForInternship(Project project) {
        if (project == null) {
            throw new DataValidateException("There is not project for create internship!");
        }
    }

    public void saveNewInternship(Project project, Internship internship, List<TeamMember> interns, LocalDate startDate, LocalDate endDate, TeamMember mentorId) {
        validateThereIsProjectForInternship(project);
        internshipService.saveNewInternship(internship,interns, startDate, endDate, mentorId);
    }
}
