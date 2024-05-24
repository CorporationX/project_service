package faang.school.projectservice.validation;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.vacancy.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VacancyValidatorTest {
    @Mock
    private VacancyValidator vacancyValidator;
    @Mock
    private ProjectRepository projectRepository;
    private VacancyDto vacancyDto;
    private Vacancy vacancy;
    private TeamMember teamMember;
    @BeforeEach
    public void init() {
        vacancyDto = new VacancyDto();
        vacancyDto.setId(1L);
        vacancyDto.setProjectId(1L);
        vacancyDto.setName("Yandex");
        vacancyDto.setCount(3);

        vacancy = new Vacancy();
        vacancy.setCandidates(List.of(new Candidate(), new Candidate()));

        teamMember = new TeamMember();
        teamMember.setId(1L);
    }

    @Test
    public void testIfVacancyNameIsNull() {
        vacancyDto.setName(null);
        doThrow(new DataValidationException("name of vacancy doesn't exist")).when(vacancyValidator).validateVacancyName(vacancyDto);
        DataValidationException thrownException = assertThrows(DataValidationException.class, () -> vacancyValidator.validateVacancyName(vacancyDto));
        assertEquals("name of vacancy doesn't exist", thrownException.getMessage());
        verify(vacancyValidator, times(1)).validateVacancyName(vacancyDto);
    }

    @Test
    public void testIfVacancyNameIsBlank() {
        vacancyDto.setName(" ");
        doThrow(new DataValidationException("name of vacancy is blank")).when(vacancyValidator).validateVacancyName(vacancyDto);
        DataValidationException thrownException = assertThrows(DataValidationException.class, () -> vacancyValidator.validateVacancyName(vacancyDto));
        assertEquals("name of vacancy is blank", thrownException.getMessage());
        verify(vacancyValidator, times(1)).validateVacancyName(vacancyDto);
    }

    @Test
    public void testIfVacancyLessThenZero(){
        vacancyDto.setCount(0);
        doThrow(new DataValidationException("count of vacancy is wrong")).when(vacancyValidator).validateCountOfVacancy(vacancyDto);
        DataValidationException thrownException = assertThrows(DataValidationException.class, () -> vacancyValidator.validateCountOfVacancy(vacancyDto));
        assertEquals("count of vacancy is wrong", thrownException.getMessage());
        verify(vacancyValidator, times(1)).validateCountOfVacancy(vacancyDto);
    }

    @Test
    public void testIfProjectDoesNotHaveId() {
        doThrow(new DataValidationException("Project with Id doesn't exist")).when(vacancyValidator).checkExistsById(vacancyDto.getProjectId());
        DataValidationException thrownException = assertThrows(DataValidationException.class, () -> vacancyValidator.checkExistsById(vacancyDto.getProjectId()));
        assertEquals("Project with Id doesn't exist", thrownException.getMessage());
        verify(vacancyValidator, times(1)).checkExistsById(vacancyDto.getProjectId());
    }

    @Test
    public void testIfCandidatesLessThenNeed() {
        doThrow(new DataValidationException("Vacancy can't be closed")).when(vacancyValidator).validateCountOfCandidate(vacancy);
        DataValidationException thrownException = assertThrows(DataValidationException.class, () -> vacancyValidator.validateCountOfCandidate(vacancy));
        assertEquals("Vacancy can't be closed", thrownException.getMessage());
        verify(vacancyValidator, times(1)).validateCountOfCandidate(vacancy);
    }

    @Test
    public void testIfVacancyDoesNotExist() {
        doThrow(new DataValidationException("Vacancy with id" + vacancyDto.getId() + "doesn't exist")).when(vacancyValidator).checkExistsVacancy(vacancyDto);
        DataValidationException thrownException = assertThrows(DataValidationException.class, () -> vacancyValidator.checkExistsVacancy(vacancyDto));
        assertEquals("Vacancy with id" + vacancyDto.getId() + "doesn't exist", thrownException.getMessage());
        verify(vacancyValidator, times(1)).checkExistsVacancy(vacancyDto);
    }

    @Test
    public void testIfTeamMemberNotOwnerInVacancy() {
        doThrow(new DataValidationException("Team member with Id " + teamMember.getId() + " is not " + TeamRole.OWNER)).when(vacancyValidator).validateTeamMember(teamMember);
        DataValidationException thrownException = assertThrows(DataValidationException.class, () -> vacancyValidator.validateTeamMember(teamMember));
        assertEquals("Team member with Id 1 is not OWNER", thrownException.getMessage());
    }
}
