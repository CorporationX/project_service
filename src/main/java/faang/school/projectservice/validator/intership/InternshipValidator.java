package faang.school.projectservice.validator.intership;

import faang.school.projectservice.model.dto.internship.InternshipDto;
import faang.school.projectservice.exception.IllegalEntityException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class InternshipValidator {
    private final ProjectJpaRepository projectJpaRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final static int MAX_INTERNSHIP_DURATION_MONTHS = 3;

    public void validateDto(InternshipDto internshipDto) {
        if (!teamMemberRepository.existsById(internshipDto.mentorId()))
            throw new EntityNotFoundException("Mentor with ID: %d does not exist".formatted(internshipDto.mentorId()));

        if (!projectJpaRepository.existsById(internshipDto.projectId()))
            throw new EntityNotFoundException("Project with ID: %d does not exist".formatted(internshipDto.projectId()));
    }

    public void validateInternshipDuration(InternshipDto internshipDto) {
        if (ChronoUnit.MONTHS.between(internshipDto.startDate(), internshipDto.endDate()) > MAX_INTERNSHIP_DURATION_MONTHS)
            throw new IllegalEntityException("Internship duration mustn't exceed %d months".formatted(MAX_INTERNSHIP_DURATION_MONTHS));
    }

    public void validateInternshipNotStarted(InternshipDto internshipDto) {
        if (internshipDto.startDate().isBefore(LocalDateTime.now()))
            throw new IllegalEntityException("Can't add new interns after the internship has started");
    }
}
