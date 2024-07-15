package faang.school.projectservice.validator;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VacancyServiceValidator {

    public void createVacancyValidator(List<TeamRole> teamRoleOwner) {
        if (!(teamRoleOwner.contains(TeamRole.OWNER))) {
            throw new IllegalArgumentException("Curator does not have the required role");
        }
    }

    public void updateVacancyValidator(Vacancy vacancy) {
        long requiredNumberCandidates = vacancy.getCount();
        long actualNumberCandidates = vacancy.getCandidates().size();

        if (requiredNumberCandidates > actualNumberCandidates) {
            throw new IllegalArgumentException("There are not enough candidates to close the vacancy");
        } else if (requiredNumberCandidates < actualNumberCandidates) {
            throw new IllegalArgumentException("The number of candidates exceeds what is required for the vacancy");
        }
    }
}
