package faang.school.projectservice.service;

import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;

    private void validateListOfInterns(List<TeamMember> interns) { // есть кого стажировать
        if (interns.isEmpty()) {
            throw new DataValidateException("Can't create an internship without interns");
        }
    }

    private void validateDurationOfInternship(LocalDate startDate, LocalDate endDate) { // стажировка не более 3-х месяцев
        Period period = Period.between(startDate, endDate);
        if (period.toTotalMonths() > 3) {
            throw new DataValidateException("The duration of the internship should not exceed 3 months!");
        }
    }

    private void validateThereIsMentorFromProject(TeamMember mentorId) { // есть ментор для стажировки
        if (mentorId == null) {
            throw new DataValidateException("There is not mentor for interns!");
        }
    }

    public void saveNewInternship(Internship internship, List<TeamMember> interns, LocalDate startDate, LocalDate endDate, TeamMember mentorId) {
        validateListOfInterns(interns);
        validateDurationOfInternship(startDate, endDate);
        validateThereIsMentorFromProject(mentorId);
        internshipRepository.save(internship);
    }
}