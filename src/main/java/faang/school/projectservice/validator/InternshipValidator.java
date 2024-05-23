package faang.school.projectservice.validator;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternshipValidator {

    private final InternshipRepository internshipRepository;

    @Value("${internship.period}")
    private long INTERNSHIP_PERIOD;

    public void validateCandidatesList(int countInternMembers) {
        if (countInternMembers == 0) {
            String message = "Intern's list is empty";
            log.info(message);
            throw new DataValidationException(message);
        }
    }

    public void validateMentorInTeamProject(TeamMember mentor, Project project) {
        if (project.getTeams().stream()
                .noneMatch(team -> team.getTeamMembers().contains(mentor))) {
            String message = "Mentor with ID: " + mentor.getId() + " isn't from project team";
            log.info(message);
            throw new DataValidationException(message);
        }
    }

    public void validateInternshipPeriod(InternshipDto internshipDto) {
        LocalDateTime startDate = internshipDto.getStartDate();
        LocalDateTime endDate = internshipDto.getEndDate();
        long monthsBetween  = ChronoUnit.MONTHS.between(YearMonth.from(startDate),
                YearMonth.from(endDate));
        int remnantDays = endDate.getDayOfMonth() - startDate.getDayOfMonth();
        if (monthsBetween > INTERNSHIP_PERIOD || (monthsBetween == INTERNSHIP_PERIOD && remnantDays > 0)) {
            String message = "The internship cannot last more than " + INTERNSHIP_PERIOD + " months";
            log.info(message);
            throw new DataValidationException(message);
        }
    }

    public void validateInternshipExistsByName(String name) {
        boolean checkResult = internshipRepository.existsByName(name);
        if (checkResult) {
            String message = "Internship " + name + " already exists!";
            log.info(message);
            throw new DataValidationException(message);
        }
    }
}
