package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static faang.school.projectservice.model.VacancyStatus.CLOSED;

public class VacancyValidator {

    public static void validateRoleForVacancyCreation(TeamMember teamMember) {
        List<TeamRole> roles = teamMember.getRoles();
        if (!roles.contains(TeamRole.OWNER) && !roles.contains(TeamRole.MANAGER)) {
            throw new ForbiddenException("Insufficient rights to create a vacancy");
        }
    }

    public static Team validateVacancyTeamInProject(Vacancy vacancy, Project project) {
        List<Team> teams = project.getTeams();
        if (teams == null) {
            throw new ForbiddenException(String.format("There is no team on the project %d", project.getId()));
        }
        Optional<Team> optionalTeam = teams.stream()
                .filter(team -> Objects.equals(team.getId(), vacancy.getTeamId()))
                .findFirst();

        if (optionalTeam.isEmpty()) {
            throw new ForbiddenException(String.format("There is no such team %d on this project %d",
                    vacancy.getTeamId(), project.getId()));
        } else {
            return optionalTeam.get();
        }
    }

    public static void guardAgainstUpdatingClosedVacancy(VacancyStatus vacancyStatus) {
        if (vacancyStatus.equals(CLOSED)) {
            throw new ForbiddenException("The vacancy is already closed and cannot be changed");
        }
    }

    public static void checkCountCandidates(Vacancy vacancy) {
        List<Candidate> candidates = vacancy.getCandidates();
        if (candidates == null) {
            throw new ForbiddenException("No candidates have been recruited yet.");
        }
        if (candidates.size() < vacancy.getCount()) {
            throw new ForbiddenException("We haven't yet recruited enough candidates to fill the vacancy.");
        }
    }
}
