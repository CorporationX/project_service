package faang.school.projectservice.validator;

import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.ProjectServiceImpl;
import faang.school.projectservice.service.TeamMemberServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenVacancyRequestValidatorTest {

    @Mock
    private ProjectServiceImpl projectService;
    @Mock
    private TeamMemberServiceImpl teamMemberService;

    @InjectMocks
    OpenVacancyRequestValidator openVacancyRequestValidator;

    @Test
    public void testValidateAndGetProject_ProjectIdIsNotPresent_Throws() {
        var projectId = 0;
        var requestDto = createOpenVacancyRequestDto(projectId, 1, null);
        when(projectService.getProjectByIdOrEmpty(projectId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> openVacancyRequestValidator.validateAndGetProject(requestDto));
    }

    @Test
    public void testValidateAndGetProject_ProjectIdIsPresent_ReturnsProjectEntity() {
        var projectId = 10L;
        var requestDto = createOpenVacancyRequestDto(projectId, 1, null);
        var expectedResult = Project.builder()
                .id(projectId)
                .name("Test project")
                .build();
        when(projectService.getProjectByIdOrEmpty(projectId))
                .thenReturn(Optional.of(expectedResult));

        var result = openVacancyRequestValidator.validateAndGetProject(requestDto);

        assertEquals(expectedResult, result);
    }

    @Test
    public void testValidateAndGetAuthor_AuthorIdIsNotPresent_Throws() {
        var authorId = 0;
        var requestDto = createOpenVacancyRequestDto(1, authorId, null);
        when(teamMemberService.getTeamMemberById(authorId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> openVacancyRequestValidator.validateAndGetAuthor(requestDto));
    }

    @ParameterizedTest
    @MethodSource("getInvalidAuthorRoles")
    public void testValidateAndGetAuthor_InvalidAuthorRoles_Throws(List<TeamRole> authorRoles) {
        var authorId = 1L;
        var requestDto = createOpenVacancyRequestDto(1, authorId, null);
        var author = TeamMember.builder()
                .id(authorId)
                .roles(authorRoles)
                .build();
        when(teamMemberService.getTeamMemberById(authorId))
                .thenReturn(Optional.of(author));

        assertThrows(DataValidationException.class,
                () -> openVacancyRequestValidator.validateAndGetAuthor(requestDto));
    }

    private static List<Arguments> getInvalidAuthorRoles() {
        return List.of(
                Arguments.of(List.of()),
                Arguments.of(List.of(TeamRole.ANALYST)),
                Arguments.of(List.of(TeamRole.INTERN, TeamRole.DESIGNER)));
    }

    @ParameterizedTest
    @MethodSource("getValidAuthorRoles")
    public void testValidateAndGetAuthor_ValidAuthorRoles_Success(List<TeamRole> authorRoles) {
        var authorId = 1L;
        var requestDto = createOpenVacancyRequestDto(1, authorId, null);
        var author = TeamMember.builder()
                .id(authorId)
                .roles(authorRoles)
                .build();
        when(teamMemberService.getTeamMemberById(authorId))
                .thenReturn(Optional.of(author));

        assertDoesNotThrow(() -> openVacancyRequestValidator.validateAndGetAuthor(requestDto));
    }

    private static List<Arguments> getValidAuthorRoles() {
        return List.of(
                Arguments.of(List.of(TeamRole.OWNER)),
                Arguments.of(List.of(TeamRole.MANAGER)),
                Arguments.of(List.of(TeamRole.OWNER, TeamRole.MANAGER)));
    }

    @Test
    public void testValidateSalary_NegativeSalary_Throws() {
        var salary = -2.0;
        var requestDto = createOpenVacancyRequestDto(1, 1, salary);

        assertThrows(DataValidationException.class,
                () -> openVacancyRequestValidator.validateSalary(requestDto));
    }

    @Test
    public void testValidateSalary_NullSalary_Success() {
        Double salary = null;
        var requestDto = createOpenVacancyRequestDto(1, 1, salary);

        assertDoesNotThrow(() -> openVacancyRequestValidator.validateSalary(requestDto));
    }

    @Test
    public void testValidateSalary_PositiveSalary_Success() {
        Double salary = 10.0;
        var requestDto = createOpenVacancyRequestDto(1, 1, salary);

        assertDoesNotThrow(() -> openVacancyRequestValidator.validateSalary(requestDto));
    }

    private static OpenVacancyRequestDto createOpenVacancyRequestDto(long projectId, long authorId, Double salary) {
        return new OpenVacancyRequestDto(
                "Test name",
                "Test description",
                projectId,
                TeamRole.ANALYST,
                1,
                authorId,
                salary,
                null,
                null);
    }
}