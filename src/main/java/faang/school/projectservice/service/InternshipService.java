package faang.school.projectservice.service;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;

    private void validateCompleteInternship(Internship internship) {
        if (internship.getStartDate() != null) {
            throw new DataValidationException("Cannot add interns after the internship has started!");
        }
        if (!internship.getStatus().equals(InternshipStatus.COMPLETED))
            throw new DataValidationException("Internship isn't completed!");
    }

    private void validateInternDoneTasks(List<TeamMember> interns, Task task) {
        interns.removeIf(members -> !task.getStatus().equals(TaskStatus.DONE));
    }

    public Internship updateInternship(Internship internship, List<TeamMember> interns, Task task) {
        validateCompleteInternship(internship);
        validateInternDoneTasks(interns, task);
        for (TeamMember intern : interns) {
            intern.setRoles(TeamRole.getAll());
        }
        return internshipRepository.save(internship);
    }
}
