package faang.school.projectservice.validator.intership;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.internship.InternshipValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class InternshipValidator {
    private final InternshipRepository internshipRepository;
    private static final long INTERNSHIP_MAX_DURATION = 3L;

    public Internship validateInternshipExists(Long id) {
        Optional<Internship> internshipToFind = internshipRepository.findById(id);
        if (internshipToFind.isEmpty()) {
            log.error("Wrong {}, or non existing internship!", id);
            throw new InternshipValidationException("This internship by this " + id + " doesn't exist!");
        }
        return internshipToFind.get();
    }

    private void validateInternshipHaveProjectAndInterns(InternshipDto internshipDto) {
        if (internshipDto.getProject() == null) {
            log.error("Missing project field!");
            throw new InternshipValidationException("Internship project field can't be null!");
        }
        if (internshipDto.getInterns() == null || internshipDto.getInterns().isEmpty()) {
            log.error("Missing interns field!");
            throw new InternshipValidationException("Internship can't be created without interns!");
        }
    }

    private void validateInternshipDuration(InternshipDto internshipDto) {
        long internshipDuration = ChronoUnit.MONTHS.between(internshipDto.getStartDate(), internshipDto.getEndDate());
        if (internshipDuration >= INTERNSHIP_MAX_DURATION) {
            log.error("Internship duration is too long!");
            throw new InternshipValidationException("The duration of internship must be not more than "
                    + INTERNSHIP_MAX_DURATION + " months!");
        }
    }

    public void validateInternship(InternshipDto internshipDto) {
        validateInternshipDuration(internshipDto);
        validateInternshipHaveProjectAndInterns(internshipDto);
    }
}
