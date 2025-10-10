package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;

import java.util.List;

import static faang.school.projectservice.model.VacancyStatus.CLOSED;

public class VacancyValidator {

    public static void validateUserAccessToCreateVacancy(TeamMember teamMember) {
        List<TeamRole> roles = teamMember.getRoles();
        if (!roles.contains(TeamRole.OWNER) && !roles.contains(TeamRole.MANAGER)) {
            throw new ForbiddenException("Insufficient rights to create a vacancy");
        }
    }

    public static void checkStatusVacancyOnCloser(VacancyStatus vacancyStatus) {
        if (vacancyStatus.equals(CLOSED)) {
            throw new RuntimeException("The vacancy is already closed and cannot be changed");
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
