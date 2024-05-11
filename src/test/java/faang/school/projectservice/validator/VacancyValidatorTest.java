package faang.school.projectservice.validator;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.service.teamMember.TeamMemberServiceImpl;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyValidatorTest {
    private static final long TEAM_MEMBER_ID = 1L;
    private static final int MIN_CANDIDATES_QUALITY = 5;

    @Mock
    private TeamMemberServiceImpl teamMemberService;
    @InjectMocks
    private VacancyValidator vacancyValidator;
    private VacancyDto vacancyDto;
    private TeamMember teamMember;

    @BeforeEach
    public void setUp() {
        vacancyDto = new VacancyDto();
        teamMember = new TeamMember();
        teamMember.setId(TEAM_MEMBER_ID);
        teamMember.setRoles(Collections.emptyList());
        vacancyDto.setCandidateIds(List.of(2L, 4L));
        vacancyDto.setStatus(VacancyStatus.CLOSED);
    }

    @Test
    public void whenCheckRolesOfVacancyCreatorThenThrowsException() {
        vacancyDto.setCreatedBy(TEAM_MEMBER_ID);
        when(teamMemberService.findById(TEAM_MEMBER_ID)).thenReturn(teamMember);
        Assert.assertThrows(VacancyValidationException.class,
                () -> vacancyValidator.checkRolesOfVacancyCreator(vacancyDto));
    }

    @Test
    public void whenCheckRolesOfVacancyUpdaterThenThrowsException() {
        vacancyDto.setUpdatedBy(TEAM_MEMBER_ID);
        when(teamMemberService.findById(TEAM_MEMBER_ID)).thenReturn(teamMember);
        Assert.assertThrows(VacancyValidationException.class,
                () -> vacancyValidator.checkRolesOfVacancyUpdater(vacancyDto));
    }

    @Test
    public void whenCheckCandidatesNumbersThenThrowsException() {
        Assert.assertThrows(VacancyValidationException.class,
                () -> vacancyValidator.checkCandidatesNumbers(vacancyDto, MIN_CANDIDATES_QUALITY));
    }
}