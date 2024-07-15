package faang.school.projectservice.vacancy;

import faang.school.projectservice.filter.VacancyFilter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.VacancyService;
import faang.school.projectservice.validator.VacancyServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @InjectMocks
    private VacancyService vacancyService;

    @Mock
    private VacancyMapper vacancyMapper;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private VacancyServiceValidator validator;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private List<VacancyFilter> vacancyFilter;
    @Mock
    private ProjectRepository projectRepository;

    private Vacancy vacancy;

    @BeforeEach
    public void setUp() {
        vacancy = new Vacancy();
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Если вакансия не привязана к проекту")
    @Test
    public void testCreateVacancyNotProject() {
        Integer count = 1;
        long idProject = 10;

        when(projectRepository.existsById(idProject)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> vacancyService.createVacancy(vacancy, count));
    }

    @DisplayName("Когда метод отрабатывает")
    @Test
    public void testCreateVacancyWhenValid() {
        Integer count = 1;
        long idProject = 1;
        long ownerId = 3;
        TeamMember teamMember = new TeamMember();
        List<TeamRole> teamRoleOwner = teamMember.getRoles();
        when(projectRepository.existsById(idProject)).thenReturn(true);
        when(teamMemberRepository.findById(ownerId)).thenReturn(teamMember);

        vacancyService.createVacancy(vacancy, count);
        validator.createVacancyValidator(teamRoleOwner);
        verify(vacancyRepository, times(1)).save(vacancy);
    }

    @DisplayName("Когда метод отрабатывает")
    @Test
    public void testUpdateVacancyWhenValid() {
        long vacancyId = 1L;
        TeamRole teamRole = TeamRole.DEVELOPER;
        when(vacancyService.getVacancy(vacancyId)).thenReturn(vacancy);

        vacancyService.updateVacancy(vacancyId, teamRole);
        validator.updateVacancyValidator(vacancy);
        verify(vacancyRepository, times(1)).save(vacancy);
    }

    @DisplayName("Когда метод отрабатывает")
    @Test
    public void testDeleteVacancyWhenValid() {
        long vacancyId = 1;

        when(vacancyService.getVacancy(vacancyId)).thenReturn(vacancy);
        vacancyService.deleteVacancy(vacancyId);
        verify(vacancyRepository, times(1)).deleteById(vacancyId);
    }

//    @DisplayName("Когда метод отрабатывает")
//    @Test
//    public void testGetVacancyPositionAndNameWhenValid() {
//        VacancyDtoFilter vacancyDtoFilters = new VacancyDtoFilter();
//        List<Vacancy> vacancies = Arrays.asList(new Vacancy(), new Vacancy(), new Vacancy());
//
//        when(vacancyRepository.findAll()).thenReturn(vacancies);
//        vacancyService.getVacancyPositionAndName(vacancyDtoFilters);
//
//    }

    @DisplayName("Если нету вакансии по id")
    @Test
    public void testGetVacancyNotVacancyDataBase() {
        long vacancyId = 100;

        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> vacancyRepository.deleteById(vacancyId));
    }

    @DisplayName("Когда метод отрабатывает")
    @Test
    public void testGetVacancyWhenValid() {
        long vacancyId = 1;

        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        vacancyService.getVacancy(vacancyId);
        verify(vacancyRepository, times(1)).findById(vacancyId);
    }

}
