package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.internship.DataValidationException;
import faang.school.projectservice.exception.internship.EntityNotFoundException;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InternshipValidator {

    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    private static final int MAX_INTERNSHIP_DURATION_MONTHS = 3;

    public void validateInternship(InternshipDto internshipDto) {
        validateProjectExists(internshipDto.getProjectId());
        validateMentor(internshipDto.getMentorId(), internshipDto.getProjectId());
        validateInterns(internshipDto.getInternsId());
        validateDates(internshipDto.getStartDate(), internshipDto.getEndDate());
    }

    private void validateProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException(String.format("Project not found by id: %s", projectId));
        }
    }

    private void validateMentor(Long mentorId, Long projectId) {
        TeamMember mentor = teamMemberRepository.findById(mentorId);
        if (mentor.getTeam() == null || !mentor.getTeam().getProject().getId().equals(projectId)) {
            throw new EntityNotFoundException(String.format("Mentor with id: %s is not a member of the project with id: %s", mentorId, projectId));
        }
    }

    private void validateInterns(List<Long> internsIds) {
        if (internsIds == null || internsIds.isEmpty()) {
            throw new DataValidationException("Interns list cannot be null or empty");
        }
    }

    private void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new DataValidationException("Start date cannot be after end date");
        }

        long durationInMonths = ChronoUnit.MONTHS.between(startDate, endDate);
        if (durationInMonths > MAX_INTERNSHIP_DURATION_MONTHS) {
            throw new DataValidationException("Internship duration exceeds the maximum allowed duration");
        }
    }
}
