package faang.school.projectservice.validator;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VacancyServiceValidator {

    private final ProjectRepository projectRepository;

    public void ensureTeamRoleOwner(TeamMember ownerMember) {
        if (!(ownerMember.getRoles().contains(TeamRole.OWNER))) {
            throw new IllegalArgumentException("Curator does not have the required role");
        }
    }

    public void checkCountCandidatesToClose(Vacancy vacancy) {
        long requiredNumberCandidates = vacancy.getCount();
        long actualNumberCandidates = vacancy.getCandidates().size();

        if (requiredNumberCandidates > actualNumberCandidates) {
            throw new IllegalArgumentException("There are not enough candidates to close the vacancy");
        } else if (requiredNumberCandidates < actualNumberCandidates) {
            throw new IllegalArgumentException("The number of candidates exceeds what is required for the vacancy");
        }
    }

    public void existByProject(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Vacancy does not apply to the project");
        }
    }

    public void existByVacancy(Optional<Vacancy> vacancy) {
        if (vacancy.isEmpty()) {
            throw new IllegalArgumentException("Vacancy with this id does not exist in the database");
        }
    }
}
