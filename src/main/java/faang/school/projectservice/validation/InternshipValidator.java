package faang.school.projectservice.validation;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.exceptions.DataValidationInternshipException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
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

    private final TeamMemberRepository teamMemberRepository;

    @Value("${internship.period}")
    private long INTERNSHIP_PERIOD;

    public void validateInternshipDto(InternshipDto internshipDto) {
        if (internshipDto.getName().isBlank()) {
            throw new DataValidationInternshipException("Internship's name can't be empty");
        }
        if (internshipDto.getDescription().isBlank()) {
            throw new DataValidationInternshipException("Internship's desc can't be empty");
        }
        if (internshipDto.getProjectId() == null) {
            throw new DataValidationInternshipException("Project ID can't be null");
        }
        if (internshipDto.getMentorId() == null) {
            throw new DataValidationInternshipException("Mentor ID can't be null");
        }
        if (internshipDto.getCandidateIds() == null) {
            throw new DataValidationInternshipException("Candidate's list can't be null");
        }
        if (internshipDto.getStartDate() == null) {
            throw new DataValidationInternshipException("Internship's start date can't be null");
        }
        if (internshipDto.getEndDate() == null) {
            throw new DataValidationInternshipException("Internship's end date can't be null");
        }
        if (internshipDto.getCreatedBy() == null) {
            throw new DataValidationInternshipException("Created by can't be null");
        }
    }

    public void validateCandidatesList(int countInternMembers) {
        // ПРоверить что список не пустой и все интерны в списке существуют
        if (countInternMembers == 0) {
            String errMessage = "Intern's list is empty";
            log.error(errMessage);
            throw new DataValidationInternshipException(errMessage);
        }

    }

    public void validateMentorInTeamProject(TeamMember mentor, Project project) {
        if (project.getTeams().stream()
                .noneMatch(team -> team.getTeamMembers().contains(mentor))) {
            String errMessage = "Mentor with ID: " + mentor.getId() + " isn't from project team";
            log.error(errMessage);
            throw new DataValidationInternshipException(errMessage);
        }
    }

    public void validateInternshipPeriod(InternshipDto internshipDto) {
        LocalDateTime startDate = internshipDto.getStartDate();
        LocalDateTime endDate = internshipDto.getEndDate();
        long monthsBetween  = ChronoUnit.MONTHS.between(YearMonth.from(startDate),
                YearMonth.from(endDate));
        int remnantDays = endDate.getDayOfMonth() - startDate.getDayOfMonth();
        if (monthsBetween > INTERNSHIP_PERIOD || (monthsBetween == INTERNSHIP_PERIOD && remnantDays > 0)) {
            String errMessage = "The internship cannot last more than " + INTERNSHIP_PERIOD + " months";
            log.error(errMessage);
            throw new DataValidationInternshipException(errMessage);
        }
    }
}
