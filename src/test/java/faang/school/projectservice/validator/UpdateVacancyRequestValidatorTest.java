package faang.school.projectservice.validator;

import faang.school.projectservice.dto.vacancy.UpdateVacancyRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UpdateVacancyRequestValidatorTest {

    @InjectMocks
    private UpdateVacancyRequestValidator updateVacancyRequestValidator;

    @Test
    public void shouldValidateCandidatesCount_throw_whenUpdateToOpenStatus() {
        var status = VacancyStatus.OPEN;
        var vacancy = Vacancy.builder().status(VacancyStatus.CLOSED).build();
        var requestDto = createUpdateVacancyRequestDto(status, null);

        assertThrows(
                DataValidationException.class,
                () -> updateVacancyRequestValidator.validateCandidatesCount(requestDto, vacancy, new ArrayList<>()));
    }

    @Test
    public void shouldValidateCandidatesCount_throw_whenUpdateToClosedStatusWithoutVacancyCount() {
        var status = VacancyStatus.CLOSED;
        var vacancy = Vacancy.builder().status(VacancyStatus.OPEN).build();
        var requestDto = createUpdateVacancyRequestDto(status, null);

        assertThrows(
                DataValidationException.class,
                () -> updateVacancyRequestValidator.validateCandidatesCount(requestDto, vacancy, new ArrayList<>()));
    }

    @Test
    public void shouldValidateCandidatesCount_throw_whenUpdateToClosedStatusIfCandidatesCountIsInvalid() {
        var status = VacancyStatus.CLOSED;
        var vacancy = Vacancy.builder()
                .id(1L)
                .status(VacancyStatus.OPEN)
                .project(Project.builder().name("Test project").build())
                .count(3)
                .build();
        var requestDto = createUpdateVacancyRequestDto(status, 5);

        assertThrows(
                DataValidationException.class,
                () -> updateVacancyRequestValidator.validateCandidatesCount(requestDto, vacancy, List.of()));
    }

    @Test
    public void shouldValidateCandidatesCount_throw_whenUpdateToPostponedStatusAndVacancyIsClosed() {
        var status = VacancyStatus.POSTPONED;
        var vacancy = Vacancy.builder().status(VacancyStatus.CLOSED).build();
        var requestDto = createUpdateVacancyRequestDto(status, null);

        assertThrows(
                DataValidationException.class,
                () -> updateVacancyRequestValidator.validateCandidatesCount(requestDto, vacancy, new ArrayList<>()));
    }

    @Test
    public void shouldValidateCandidatesCount_success_whenStatusIsNull() {
        var requestDto = createUpdateVacancyRequestDto(null, null);

        assertDoesNotThrow(() -> updateVacancyRequestValidator.validateCandidatesCount(requestDto, new Vacancy(),
                new ArrayList<>()));
    }

    @Test
    public void shouldValidateCandidatesCount_success_whenStatusIsNotChanged() {
        var status = VacancyStatus.OPEN;
        var vacancy = Vacancy.builder().status(status).build();
        var requestDto = createUpdateVacancyRequestDto(status, null);

        assertDoesNotThrow(
                () -> updateVacancyRequestValidator.validateCandidatesCount(requestDto, vacancy, new ArrayList<>()));
    }

    @Test
    public void shouldValidateCandidatesCount_success_whenUpdateToClosedStatusIfCandidatesCountIsValid() {
        var status = VacancyStatus.CLOSED;
        var vacancy = Vacancy.builder().status(VacancyStatus.OPEN).build();
        var acceptedCandidates = List.of(new Candidate(), new Candidate());
        var requestDto = createUpdateVacancyRequestDto(status, 2);

        assertDoesNotThrow(() -> updateVacancyRequestValidator.validateCandidatesCount(requestDto, vacancy,
                acceptedCandidates));
    }

    @Test
    public void shouldValidateCandidatesCount_success_whenUpdateToPostponedStatusAndVacancyIsOpened() {
        var status = VacancyStatus.POSTPONED;
        var vacancy = Vacancy.builder().status(VacancyStatus.OPEN).build();
        var requestDto = createUpdateVacancyRequestDto(status, null);

        assertDoesNotThrow(() -> updateVacancyRequestValidator.validateCandidatesCount(requestDto, vacancy,
                new ArrayList<>()));
    }

    @ParameterizedTest
    @MethodSource("getInvalidUpdaterRoles")
    public void shouldValidateUpdaterRole_throw_whenSomeUpdaterRolesAreInvalid(List<TeamRole> updaterRoles) {
        var teamMemberUpdaterId = 1L;
        var updaterTeamMember = TeamMember.builder().id(teamMemberUpdaterId).roles(updaterRoles).build();

        assertThrows(
                DataValidationException.class,
                () -> updateVacancyRequestValidator.validateUpdaterRole(updaterTeamMember));
    }

    private static List<Arguments> getInvalidUpdaterRoles() {
        return List.of(
                Arguments.of(List.of()),
                Arguments.of(List.of(TeamRole.ANALYST)),
                Arguments.of(List.of(TeamRole.INTERN, TeamRole.DESIGNER)));
    }

    @ParameterizedTest
    @MethodSource("getValidUpdaterRoles")
    public void shouldValidateAndGetAuthor_success_whenAllAuthorRolesAreValid(List<TeamRole> updaterRoles) {
        var teamMemberUpdaterId = 1L;
        var updaterTeamMember = TeamMember.builder().id(teamMemberUpdaterId).roles(updaterRoles).build();

        assertDoesNotThrow(() -> updateVacancyRequestValidator.validateUpdaterRole(updaterTeamMember));
    }

    private static List<Arguments> getValidUpdaterRoles() {
        return List.of(Arguments.of(List.of(TeamRole.OWNER)), Arguments.of(List.of(TeamRole.MANAGER)),
                Arguments.of(List.of(TeamRole.OWNER, TeamRole.MANAGER)));
    }

    private static UpdateVacancyRequestDto createUpdateVacancyRequestDto(VacancyStatus status,
            Integer requiredCandidatesCount) {
        return UpdateVacancyRequestDto.builder()
                .vacancyId(1L)
                .teamMemberUpdaterId(1L)
                .name("Test name")
                .description("Test description")
                .position(TeamRole.ANALYST)
                .status(status)
                .requiredCandidatesCount(requiredCandidatesCount)
                .build();
    }
}