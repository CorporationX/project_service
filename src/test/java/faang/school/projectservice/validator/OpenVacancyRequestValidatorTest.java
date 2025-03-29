package faang.school.projectservice.validator;

import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OpenVacancyRequestValidatorTest {

    @InjectMocks
    OpenVacancyRequestValidator openVacancyRequestValidator;

    @ParameterizedTest
    @MethodSource("getInvalidAuthorRoles")
    public void shouldValidateAuthor_throw_whenSomeAuthorRolesAreInvalid(List<TeamRole> authorRoles) {
        var authorId = 1L;
        var author = TeamMember.builder().id(authorId).roles(authorRoles).build();

        assertThrows(DataValidationException.class, () -> openVacancyRequestValidator.validateAuthor(author));
    }

    private static List<Arguments> getInvalidAuthorRoles() {
        return List.of(
                Arguments.of(List.of()),
                Arguments.of(List.of(TeamRole.ANALYST)),
                Arguments.of(List.of(TeamRole.INTERN, TeamRole.DESIGNER)));
    }

    @ParameterizedTest
    @MethodSource("getValidAuthorRoles")
    public void shouldValidateAuthor_success_whenAllAuthorRolesAreValid(List<TeamRole> authorRoles) {
        var authorId = 1L;
        var author = TeamMember.builder().id(authorId).roles(authorRoles).build();

        assertDoesNotThrow(() -> openVacancyRequestValidator.validateAuthor(author));
    }

    private static List<Arguments> getValidAuthorRoles() {
        return List.of(
                Arguments.of(List.of(TeamRole.OWNER)),
                Arguments.of(List.of(TeamRole.MANAGER)),
                Arguments.of(List.of(TeamRole.OWNER, TeamRole.MANAGER)));
    }

    @Test
    public void shouldValidateSalary_throw_whenSalaryIsNegative() {
        var salary = -2.0;
        var requestDto = createOpenVacancyRequestDto(salary);

        assertThrows(DataValidationException.class, () -> openVacancyRequestValidator.validateSalary(requestDto));
    }

    @Test
    public void shouldValidateSalary_success_whenSalaryIsNull() {
        Double salary = null;
        var requestDto = createOpenVacancyRequestDto(salary);

        assertDoesNotThrow(() -> openVacancyRequestValidator.validateSalary(requestDto));
    }

    @Test
    public void shouldValidateSalary_success_whenSalaryIsPositive() {
        Double salary = 10.0;
        var requestDto = createOpenVacancyRequestDto(salary);

        assertDoesNotThrow(() -> openVacancyRequestValidator.validateSalary(requestDto));
    }

    private static OpenVacancyRequestDto createOpenVacancyRequestDto(Double salary) {
        return OpenVacancyRequestDto.builder()
                .name("Test name")
                .description("Test description")
                .projectId(1)
                .position(TeamRole.ANALYST)
                .requiredCandidatesCount(1)
                .authorId(1)
                .salary(salary)
                .build();
    }
}