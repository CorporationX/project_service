package faang.school.projectservice.validator;

import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

@Component
public class OpenVacancyRequestValidator {

    public TeamMember validateAuthor(TeamMember author) {
        var isRightRole = author.getRoles()
                .stream()
                .anyMatch(role -> role == TeamRole.OWNER || role == TeamRole.MANAGER);
        if (!isRightRole) {
            throw new DataValidationException(
                    "Current author roles are %s. Only OWNER and MANAGER is possible".formatted(
                            String.join(",", author.getRoles().stream().map(Enum::toString).toList())));
        }

        return author;
    }

    public void validateSalary(OpenVacancyRequestDto requestDto) {
        if (requestDto.salary() != null && requestDto.salary() <= 0) {
            throw new DataValidationException("Negative or null salary is crazy");
        }
    }
}
