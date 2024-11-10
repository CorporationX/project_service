package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VacancyServiceValidator {

    public boolean validationCompletedVacancy(Vacancy vacancy) {
        int count = vacancy.getCount();
        List<Candidate> listCandidates = vacancy.getCandidates().stream()
                .filter(candidate -> candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .toList();
        return listCandidates.size() >= count;
    }

    public boolean verificationOfCurator(Long userId, List<TeamMember> teamMembers, List<Project> listOfProjectOwners) {
        return verificationOfCuratorByUserId(listOfProjectOwners, userId) && rolesContainCurator(teamMembers);
    }

    private boolean verificationOfCuratorByUserId(List<Project> listOfProjectOwners, Long userId) {
        List<Long> userProjectIds = listOfProjectOwners.stream()
                .map(Project::getOwnerId).toList();
        return userProjectIds.contains(userId);
    }

    private boolean rolesContainCurator(List<TeamMember> teamMembers) {
        List<TeamRole> roles = teamMembers.stream()
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .distinct()
                .toList();
        return roles.contains(TeamRole.OWNER);
    }
}
