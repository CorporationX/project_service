package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.service.teamMember.TeamMemberServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class VacancyValidator {
    private final TeamMemberServiceImpl teamMemberService;

    public boolean checkRolesOfVacancyCreator(VacancyDto vacancy) {
        TeamMember creator = teamMemberService.findById(vacancy.getCreatedBy());
        if (!creator.getRoles().contains(TeamRole.OWNER)) {
            throw new VacancyValidationException("you can't create new vacancies. You have not role owner");
        }
        return true;
    }

    public boolean checkRolesOfVacancyUpdater(VacancyDto vacancy) {
        TeamMember updater = teamMemberService.findById(vacancy.getUpdatedBy());
        if (!updater.getRoles().contains(TeamRole.OWNER) || !updater.getRoles().contains(TeamRole.MANAGER)) {
            throw new VacancyValidationException("you can't update vacancies. You have not roles owner or manager");
        }
        return true;
    }

    public boolean checkCandidatesNumbers(VacancyDto vacancy, int minCandidatesNumbers) {
        if (vacancy.getStatus() == VacancyStatus.CLOSED) {
            if (vacancy.getCandidateIds().size() < minCandidatesNumbers) {
                throw new VacancyValidationException(String.format("min number of candidates is %d", minCandidatesNumbers));
            }
        }
        return true;
    }
}