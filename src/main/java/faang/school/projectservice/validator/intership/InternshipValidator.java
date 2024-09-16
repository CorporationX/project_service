package faang.school.projectservice.validator.intership;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.internship.InternshipValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class InternshipValidator {

    private final TeamMemberRepository teamMemberRepository;
    private final InternshipRepository internshipRepository;

    public Internship validateInternshipExists(Long id) {
        if (internshipRepository.findById(id).isEmpty()) {
            log.error("Wrong id, or non existing internship!");
            throw new InternshipValidationException("This internship by this id doesn't exist!");
        }
        return internshipRepository.findById(id).get();
    }

    public void validateInternshipHaveProjectAndInterns(InternshipDto internshipDto) {
        if (internshipDto.getProject() == null) {
            log.error("Missing project field!");
            throw new InternshipValidationException("Internship project field can't be null!");
        }
        if (internshipDto.getInterns() == null || internshipDto.getInterns().isEmpty()) {
            log.error("Missing interns field!");
            throw new InternshipValidationException("Internship can't be created without interns!");
        }
    }

    public void validateInternshipDuration(InternshipDto internshipDto) {
        long internshipDuration = ChronoUnit.MONTHS.between(internshipDto.getStartDate(), internshipDto.getEndDate());
        if (internshipDuration >= 3) {
            log.error("Internship duration is too long!");
            throw new InternshipValidationException("The duration of internship must be not more than 3 months!");
        }
    }

    public void validateInternshipGotMentor(InternshipDto internshipDto) {
        teamMemberRepository.findById(internshipDto.getMentorId().getId());
    }
}
