package faang.school.projectservice.validator;

import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.ProjectServiceImpl;
import faang.school.projectservice.service.TeamMemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenVacancyRequestValidator {

    private final ProjectServiceImpl projectServiceImpl;
    private final TeamMemberServiceImpl teamMemberServiceImpl;

    public @NonNull Project validateProject(OpenVacancyRequestDto requestDto) {
        return projectServiceImpl.getProjectByIdOrEmpty(requestDto.projectId())
                .orElseThrow(() -> new DataValidationException(
                        "Project with id %d is not found".formatted(requestDto.projectId())));
    }

    public TeamMember validateAuthor(OpenVacancyRequestDto requestDto) {
        var author = teamMemberServiceImpl.getTeamMemberById(requestDto.authorId())
                .orElseThrow(() -> new DataValidationException(
                        "Author with id %d is not found".formatted(requestDto.authorId())));

        var isRightRole = author.getRoles()
                .stream()
                .anyMatch(role -> role == TeamRole.OWNER || role == TeamRole.MANAGER);
        if (!isRightRole) {
            throw new DataValidationException(
                    "Current author roles are %s. Only OWNER and MANAGER is possible".formatted(
                            String.join(
                                    ",",
                                    author.getRoles().stream().map(Enum::toString).toList())));
        }

        return author;
    }

    public void validateSalary(OpenVacancyRequestDto requestDto) {
        if (requestDto.salary() != null && requestDto.salary() <= 0) {
            throw new DataValidationException("Negative or null salary is crazy");
        }
    }
}
