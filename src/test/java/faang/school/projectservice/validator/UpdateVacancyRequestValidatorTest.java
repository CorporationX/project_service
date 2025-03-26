package faang.school.projectservice.validator;

import faang.school.projectservice.dto.vacancy.UpdateVacancyRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.TeamMemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateVacancyRequestValidatorTest {

    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private TeamMemberService teamMemberService;

    @InjectMocks
    private UpdateVacancyRequestValidator updateVacancyRequestValidator;

    @Test
    public void shouldValidateAndGetVacancy_throw_whenVacancyIdIsNotPresented() {
        var vacancyId = 0L;
        var requestDto = createUpdateVacancyRequestDto(vacancyId, 0L, null, null);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());

        assertThrows(
                DataValidationException.class,
                () -> updateVacancyRequestValidator.validateAndGetVacancy(requestDto));
    }

    @Test
    public void shouldValidateAndGetVacancy_returnsVacancyEntity_whenVacancyIdIsPresented() {
        var vacancyId = 10L;
        var requestDto = createUpdateVacancyRequestDto(vacancyId, 0L, null, null);
        var expectedResult = Vacancy.builder().id(vacancyId).name("Test vacancy").build();
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(expectedResult));

        var result = updateVacancyRequestValidator.validateAndGetVacancy(requestDto);

        assertEquals(expectedResult, result);
    }

    @Test
    public void shouldValidateCandidatesCount_throw_whenUpdateToOpenStatus() {
        var status = VacancyStatus.OPEN;
        var vacancy = Vacancy.builder().status(VacancyStatus.CLOSED).build();
        var requestDto = createUpdateVacancyRequestDto(10L, 1L, status, null);

        assertThrows(
                DataValidationException.class,
                () -> updateVacancyRequestValidator.validateCandidatesCount(requestDto, vacancy, new ArrayList<>()));
    }

    @Test
    public void shouldValidateCandidatesCount_throw_whenUpdateToClosedStatusWithoutVacancyCount() {
        var status = VacancyStatus.CLOSED;
        var vacancy = Vacancy.builder().status(VacancyStatus.OPEN).build();
        var requestDto = createUpdateVacancyRequestDto(10L, 1L, status, null);

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
        var requestDto = createUpdateVacancyRequestDto(10L, 1L, status, 5);

        assertThrows(
                DataValidationException.class,
                () -> updateVacancyRequestValidator.validateCandidatesCount(requestDto, vacancy, List.of()));
    }

    @Test
    public void shouldValidateCandidatesCount_throw_whenUpdateToPostponedStatusAndVacancyIsClosed() {
        var status = VacancyStatus.POSTPONED;
        var vacancy = Vacancy.builder().status(VacancyStatus.CLOSED).build();
        var requestDto = createUpdateVacancyRequestDto(10L, 1L, status, null);

        assertThrows(
                DataValidationException.class,
                () -> updateVacancyRequestValidator.validateCandidatesCount(requestDto, vacancy, new ArrayList<>()));
    }

    @Test
    public void shouldValidateCandidatesCount_success_whenStatusIsNull() {
        var requestDto = createUpdateVacancyRequestDto(10L, 1L, null, null);

        assertDoesNotThrow(() -> updateVacancyRequestValidator.validateCandidatesCount(
                requestDto,
                new Vacancy(),
                new ArrayList<>()));
    }

    @Test
    public void shouldValidateCandidatesCount_success_whenStatusIsNotChanged() {
        var status = VacancyStatus.OPEN;
        var vacancy = Vacancy.builder().status(status).build();
        var requestDto = createUpdateVacancyRequestDto(10L, 1L, status, null);

        assertDoesNotThrow(() -> updateVacancyRequestValidator.validateCandidatesCount(
                requestDto,
                vacancy,
                new ArrayList<>()));
    }

    @Test
    public void shouldValidateCandidatesCount_success_whenUpdateToClosedStatusIfCandidatesCountIsValid() {
        var status = VacancyStatus.CLOSED;
        var vacancy = Vacancy.builder().status(VacancyStatus.OPEN).build();
        var acceptedCandidates = List.of(new Candidate(), new Candidate());
        var requestDto = createUpdateVacancyRequestDto(10L, 1L, status, 2);

        assertDoesNotThrow(() -> updateVacancyRequestValidator.validateCandidatesCount(
                requestDto,
                vacancy,
                acceptedCandidates));
    }

    @Test
    public void shouldValidateCandidatesCount_success_whenUpdateToPostponedStatusAndVacancyIsOpened() {
        var status = VacancyStatus.POSTPONED;
        var vacancy = Vacancy.builder().status(VacancyStatus.OPEN).build();
        var requestDto = createUpdateVacancyRequestDto(10L, 1L, status, null);

        assertDoesNotThrow(() -> updateVacancyRequestValidator.validateCandidatesCount(
                requestDto,
                vacancy,
                new ArrayList<>()));
    }

    @Test
    public void shouldValidateUpdaterRole_throw_whenTeamMemberUpdaterIdIsNotPresented() {
        var teamMemberUpdaterId = 0L;
        var requestDto = createUpdateVacancyRequestDto(10L, teamMemberUpdaterId, null, null);
        when(teamMemberService.getTeamMemberById(teamMemberUpdaterId))
                .thenReturn(Optional.empty());

        assertThrows(
                DataValidationException.class,
                () -> updateVacancyRequestValidator.validateUpdaterRole(requestDto));
    }

    @ParameterizedTest
    @MethodSource("getInvalidUpdaterRoles")
    public void shouldValidateUpdaterRole_throw_whenSomeUpdaterRolesAreInvalid(List<TeamRole> updaterRoles) {
        var teamMemberUpdaterId = 1L;
        var requestDto = createUpdateVacancyRequestDto(10L, teamMemberUpdaterId, null, null);
        var updaterTeamMember = TeamMember.builder().id(teamMemberUpdaterId).roles(updaterRoles).build();
        when(teamMemberService.getTeamMemberById(teamMemberUpdaterId)).thenReturn(Optional.of(updaterTeamMember));

        assertThrows(
                DataValidationException.class,
                () -> updateVacancyRequestValidator.validateUpdaterRole(requestDto));
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
        var requestDto = createUpdateVacancyRequestDto(10L, teamMemberUpdaterId, null, null);
        var updaterTeamMember = TeamMember.builder().id(teamMemberUpdaterId).roles(updaterRoles).build();
        when(teamMemberService.getTeamMemberById(teamMemberUpdaterId))
                .thenReturn(Optional.of(updaterTeamMember));

        assertDoesNotThrow(() -> updateVacancyRequestValidator.validateUpdaterRole(requestDto));
    }

    private static List<Arguments> getValidUpdaterRoles() {
        return List.of(Arguments.of(List.of(TeamRole.OWNER)), Arguments.of(List.of(TeamRole.MANAGER)), Arguments.of(List.of(TeamRole.OWNER, TeamRole.MANAGER)));
    }

    private static UpdateVacancyRequestDto createUpdateVacancyRequestDto(
            long vacancyId,
            long updaterId,
            @Nullable VacancyStatus status,
            @Nullable Integer requiredCandidatesCount) {
        return UpdateVacancyRequestDto.builder()
                .vacancyId(vacancyId)
                .teamMemberUpdaterId(updaterId)
                .name("Test name")
                .description("Test description")
                .position(TeamRole.ANALYST)
                .status(status)
                .requiredCandidatesCount(requiredCandidatesCount)
                .build();
    }
}