package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.TeamMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Bulgakov
 */
@ExtendWith(MockitoExtension.class)
public class VacancyValidatorTest {
    @Mock
    private TeamMemberService teamMemberService;

    private VacancyValidator vacancyValidator;

    @BeforeEach
    void setUp() {
        vacancyValidator = new VacancyValidator(teamMemberService);
    }

    @Test
    void validateUser_ValidUserDto_NoExceptionThrown() {
        UserDto user = new UserDto();
        user.setId(1L);

        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setCreatedBy(1L);

        vacancyValidator.validateUser(user, vacancyDto);
    }

    @Test
    void validateUser_InvalidUserDto_DataValidationExceptionThrown() {
        UserDto user = new UserDto();
        user.setId(1L);

        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setCreatedBy(2L);

        assertThrows(DataValidationException.class, () ->
                vacancyValidator.validateUser(user, vacancyDto));
    }

    @Test
    void validateVacancy_ValidVacancyDto_NoExceptionThrown() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setProjectId(1L);
        vacancyDto.setCreatedBy(1L);

        vacancyValidator.validateVacancy(vacancyDto);
    }

    @Test
    void validateVacancy_InvalidVacancyDto_DataValidationExceptionThrown() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setProjectId(null);
        vacancyDto.setCreatedBy(null);

        assertThrows(DataValidationException.class, () ->
                vacancyValidator.validateVacancy(vacancyDto));
    }

    @Test
    void validateSupervisorRole_ValidSupervisorRole_NoExceptionThrown() {
        long supervisorId = 1L;

        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.OWNER, TeamRole.MANAGER));

        when(teamMemberService.getTeamMember(supervisorId)).thenReturn(teamMember);

        vacancyValidator.validateSupervisorRole(supervisorId);

        verify(teamMemberService, times(1)).getTeamMember(supervisorId);
    }

    @Test
    void validateSupervisorRole_InvalidSupervisorRole_DataValidationExceptionThrown() {
        long supervisorId = 1L;

        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.DEVELOPER));

        when(teamMemberService.getTeamMember(supervisorId)).thenReturn(teamMember);

        assertThrows(DataValidationException.class, () ->
                vacancyValidator.validateSupervisorRole(supervisorId));

        verify(teamMemberService, times(1)).getTeamMember(supervisorId);
    }

    @Test
    void validateCandidateRole_AllCandidatesHaveRole_NoExceptionThrown() {
        Vacancy vacancy = new Vacancy();
        List<Candidate> candidates = new ArrayList<>();
        Candidate candidate1 = new Candidate();
        candidate1.setId(1L);
        candidate1.setCandidateStatus(CandidateStatus.ACCEPTED);
        candidates.add(candidate1);

        List<TeamRole> roles = List.of(TeamRole.OWNER);

        TeamMember owner = new TeamMember();
        owner.setRoles(roles);

        vacancy.setCandidates(candidates);

        when(teamMemberService.getAllTeamMembersByIds(List.of(1L)))
                .thenReturn(List.of(owner));

        vacancyValidator.validateCandidateRole(vacancy);

        verify(teamMemberService, times(1)).getAllTeamMembersByIds(List.of(1L));
    }

    @Test
    void validateCandidateRole_NotAllCandidatesHaveRole_DataValidationExceptionThrown() {
        Vacancy vacancy = new Vacancy();
        List<Candidate> candidates = new ArrayList<>();
        Candidate candidate1 = new Candidate();
        candidate1.setId(1L);
        candidate1.setCandidateStatus(CandidateStatus.ACCEPTED);
        candidates.add(candidate1);
        vacancy.setCandidates(candidates);

        when(teamMemberService.getAllTeamMembersByIds(List.of(1L)))
                .thenReturn(List.of(new TeamMember(), new TeamMember()));

        assertThrows(DataValidationException.class, () ->
                vacancyValidator.validateCandidateRole(vacancy));

        verify(teamMemberService, times(1)).getAllTeamMembersByIds(List.of(1L));
    }

    @Test
    void validateForCloseVacancy_EnoughCandidatesRecruited_NoExceptionThrown() {
        Vacancy vacancy = new Vacancy();
        vacancy.setCandidates(List.of(new Candidate(), new Candidate()));
        vacancy.setCount(2);

        vacancyValidator.validateForCloseVacancy(vacancy);
    }

    @Test
    void validateForCloseVacancy_InvalidVacancy_NotEnoughCandidatesRecruited_DataValidationExceptionThrown() {
        Vacancy vacancy = new Vacancy();
        vacancy.setCandidates(List.of(new Candidate()));
        vacancy.setCount(2);

        assertThrows(DataValidationException.class, () ->
                vacancyValidator.validateForCloseVacancy(vacancy));
    }

}
