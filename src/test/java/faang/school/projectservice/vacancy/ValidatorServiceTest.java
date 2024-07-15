package faang.school.projectservice.vacancy;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.validator.VacancyServiceValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ValidatorServiceTest {

    @InjectMocks
    private VacancyServiceValidator validator;

    @DisplayName("Когда у куратора нету подходящий роли")
    @Test
    public void testCreateVacancyValidatorTeamRole() {
        List<TeamRole> teamRoleOwner = Arrays.asList(TeamRole.ANALYST);

        assertThrows(IllegalArgumentException.class, () -> validator.createVacancyValidator(teamRoleOwner));
    }

    @DisplayName("Когда метод проходит")
    @Test
    public void testCreateVacancyValidatorWhenValid() {
        List<TeamRole> teamRoleOwner = Arrays.asList(TeamRole.OWNER);

        assertDoesNotThrow(() -> validator.createVacancyValidator(teamRoleOwner));
    }

    @DisplayName("Если кандидатов меньше чем требуется для вакансии")
    @Test
    public void testUpdateVacancyValidatorLessCandidate() {
        Vacancy vacancy = new Vacancy();
        vacancy.setCount(2);
        vacancy.setCandidates(Arrays.asList(new Candidate()));

        assertThrows(IllegalArgumentException.class, () -> validator.updateVacancyValidator(vacancy));
    }

    @DisplayName("Если кандидатов больше чем мест в вакансии")
    @Test
    public void testUpdateVacancyValidatorMoreCandidate() {
        Vacancy vacancy = new Vacancy();
        vacancy.setCount(1);
        vacancy.setCandidates(Arrays.asList(new Candidate(), new Candidate(), new Candidate()));

        assertThrows(IllegalArgumentException.class, () -> validator.updateVacancyValidator(vacancy));
    }

    @DisplayName("Когда метод проходит")
    @Test
    public void testUpdateVacancyValidatorWhenValid() {
        Vacancy vacancy = new Vacancy();
        vacancy.setCount(2);
        vacancy.setCandidates(Arrays.asList(new Candidate(), new Candidate()));

        assertDoesNotThrow(() -> validator.updateVacancyValidator(vacancy));
    }
}