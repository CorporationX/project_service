package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.IllegalEntityException;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class InternshipValidator {
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamRepository;

    private static final int MAX_INTERNSHIP_DURATION_MONTHS = 3;

    public void validateDto(InternshipDto internshipDto) {
        if (!teamRepository.existsById(internshipDto.mentorId())) {
            throw new EntityNotFoundException(String.format("Mentor with ID: %d not found.", internshipDto.mentorId()));
        }
        if (!projectRepository.existsById(internshipDto.projectId())) {
            throw new EntityNotFoundException(String.format("Project with ID: %d not found.", internshipDto.projectId()));
        }
    }

    public void validateInternshipDuration(InternshipDto internshipDto) {
        if (ChronoUnit.MONTHS.between(internshipDto.startDate(), internshipDto.endDate()) > MAX_INTERNSHIP_DURATION_MONTHS) {
            throw new IllegalEntityException("Internship duration must not exceed " + MAX_INTERNSHIP_DURATION_MONTHS + " months.");
        }
    }

    public void validateInternshipNotStarted(InternshipDto internshipDto) {
        if (internshipDto.startDate().isBefore(LocalDateTime.now())) {
            throw new IllegalEntityException("Cannot add new interns after the internship has started.");
        }
    }
}
