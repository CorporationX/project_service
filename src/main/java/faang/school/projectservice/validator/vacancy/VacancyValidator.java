package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.team.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Alexander Bulgakov
 */

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    private final TeamMemberService teamMemberService;

    public void validateId(Long id) {
        if (id == null) {
            throw new DataValidationException("This id is null!");
        }
    }
    public void validateVacancyController(VacancyDto dto) {
        if (null == dto.getProjectId()) {
            throw new DataValidationException("The vacancy does not belong to any project!");
        } else if (null == dto.getCreatedBy()) {
            throw new DataValidationException("The vacancy does not have a supervisor!");
        }
    }

    public void validateCreateVacancy(long id) {
        TeamMember teamMember = teamMemberService.getTeamMember(id);
        if (!teamMember.getRoles().contains(TeamRole.OWNER)) {
            throw new DataValidationException("The supervisor does not have the appropriate role!");
        }
    }

    public void validateForUpdateVacancy(Vacancy vacancy) {
        if (teamMemberService.getAllTeamMembersByIds(vacancy.getCandidates()
                .stream()
                .filter(candidate -> candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .map(Candidate::getId)
                .toList())
                .stream()
                .anyMatch(teamMember -> null == teamMember.getRoles())) {
            throw new DataValidationException("Not all candidates have a role on the project");
        }
    }

    public void validateForCloseVacancy(Vacancy vacancy) {
        if (vacancy.getCandidates().size() < vacancy.getCount()) {
            throw new DataValidationException("This vacancy can't be closed, " +
                    "the required number of candidates hasn't been recruited!!");
        }
    }
}
