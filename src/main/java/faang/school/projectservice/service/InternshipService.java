package faang.school.projectservice.service;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;


@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;

    private void validateListOfInternsAndThereIsMentor(Internship internship) {
        if (internship.getInterns() == null) {
            throw new DataValidationException("Can't create an internship without interns");
        }
        if (internship.getMentorId() == null) {
            throw new DataValidationException("There is not mentor for interns!");
        }
        if (internship.getStartDate().isAfter(internship.getEndDate().plus(3, ChronoUnit.MONTHS))) {
            throw new DataValidationException("Internship date was entered incorrectly!");
        } // true - конец стажировки +3 мес.; не позже начала стажировки - false
    }

    public Internship saveNewInternship(Internship internship) {
        validateListOfInternsAndThereIsMentor(internship);
        return internshipRepository.save(internship);
    }
}