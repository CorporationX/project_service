package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.exeption.ForbiddenException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VacancyValidator {

    public void validateRole(TeamMember author) {

        if (author == null) {
            throw new ForbiddenException("User is not a member of this project");
        }

        if (!author.getRoles().contains(TeamRole.OWNER) && !author.getRoles().contains(TeamRole.MANAGER)) {
            throw new ForbiddenException("Only OWNER or MANAGER can modify vacancies");
        }
    }
}
