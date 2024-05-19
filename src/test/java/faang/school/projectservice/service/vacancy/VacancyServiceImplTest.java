package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.vacancy.VacancyValidationException;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.candidate.CandidateService;
import faang.school.projectservice.service.teamMember.TeamMemberServiceImpl;
import faang.school.projectservice.service.vacancy.filter.VacancyFilter;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceImplTest {
    private static final long VACANCY_ID = 1L;
    private static final long PROJECT_ID = 1L;
    private static final long CANDIDATE_ID = 1L;

    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private CandidateService candidateService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberServiceImpl teamMemberService;
    @Mock
    private VacancyMapper vacancyMapper;
    @Mock
    private VacancyValidator vacancyValidator;
    @Mock
    private VacancyFilter vacancyFilter;
    private VacancyServiceImpl vacancyService;
    private Vacancy vacancy;
    private VacancyDto vacancyDto;
    private Project project;
    private Candidate candidate;
    private VacancyFilterDto vacancyFilterDto;
    private List<VacancyFilter> filters;

    @BeforeEach
    void setUp() {
        filters = List.of(vacancyFilter);
        vacancyService = new VacancyServiceImpl(vacancyRepository, candidateService, projectRepository,
                teamMemberService, vacancyMapper, vacancyValidator, filters);
        project = new Project();
        project.setId(PROJECT_ID);
        candidate = new Candidate();
        candidate.setId(CANDIDATE_ID);
        vacancy = new Vacancy();
        vacancy.setId(VACANCY_ID);
        vacancyDto = new VacancyDto();
        vacancyDto.setId(VACANCY_ID);
        vacancyDto.setProjectId(PROJECT_ID);
        vacancyDto.setCandidateIds(List.of(CANDIDATE_ID));
        vacancyFilterDto = new VacancyFilterDto();
    }


    @Test
    public void whenCreateVacancyAndValidationFailedThenThrowException() {
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        when(vacancyValidator.checkRolesOfVacancyCreator(vacancyDto)).thenThrow(VacancyValidationException.class);
        Assert.assertThrows(VacancyValidationException.class,
                () -> vacancyService.create(vacancyDto));
    }

    @Test
    public void whenCreateVacancyThenReturnVacancyDto() {
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(vacancyMapper.toDto(any())).thenReturn(vacancyDto);
        VacancyDto actual = vacancyService.create(vacancyDto);
        assertThat(actual).isEqualTo(vacancyDto);
    }

    @Test
    public void whenUpdateVacancyWithWrongIdThenThrowException() {
      when(vacancyRepository.findById(VACANCY_ID)).thenThrow(EntityNotFoundException.class);
        Assert.assertThrows(EntityNotFoundException.class,
                () -> vacancyService.update(vacancyDto));
    }

    @Test
    public void whenUpdateVacancyAndValidationOfUpdaterFailedThenThrowException() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(vacancyValidator.checkRolesOfVacancyUpdater(vacancyDto)).thenThrow(VacancyValidationException.class);
        Assert.assertThrows(VacancyValidationException.class,
                () -> vacancyService.update(vacancyDto));
    }

    @Test
    public void whenUpdateVacancyAndValidationOfCandidateNumbersFailedThenThrowException() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(vacancyValidator.checkRolesOfVacancyUpdater(vacancyDto)).thenReturn(true);
        when(vacancyValidator.checkCandidatesNumbers(any(), anyInt())).thenThrow(VacancyValidationException.class);
        Assert.assertThrows(VacancyValidationException.class,
                () -> vacancyService.update(vacancyDto));
    }

    @Test
    public void whenUpdateVacancySuccessfullyThenReturnTrue() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(vacancyValidator.checkRolesOfVacancyUpdater(vacancyDto)).thenReturn(true);
        when(vacancyValidator.checkCandidatesNumbers(any(), anyInt())).thenReturn(true);
        VacancyDto actual = vacancyService.update(vacancyDto);
        assertThat(actual).isEqualTo(vacancyDto);
    }


    @Test
    public void wheDeleteVacancyWithWrongIdThenThrowException() {
        when(vacancyRepository.findById(VACANCY_ID)).thenThrow(EntityNotFoundException.class);
        Assert.assertThrows(EntityNotFoundException.class,
                () -> vacancyService.delete(VACANCY_ID));
    }

    @Test
    public void wheDeleteVacancyThenThenReturnTrue() {
        candidate.setCandidateStatus(CandidateStatus.ACCEPTED);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(candidateService.findById(CANDIDATE_ID)).thenReturn(candidate);
        assertThat(vacancyService.delete(VACANCY_ID)).isTrue();
    }

    @Test
    public void whenFindDtoByIdWithWrongIdThenThrowException() {
        when(vacancyRepository.findById(VACANCY_ID)).thenThrow(EntityNotFoundException.class);
        Assert.assertThrows(EntityNotFoundException.class,
                () -> vacancyService.findById(VACANCY_ID));
    }

    @Test
    public void whenFindDtoByIdThenReturnVacancyDto() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);
        VacancyDto actual = vacancyService.findById(VACANCY_ID);
        assertThat(actual).isEqualTo(vacancyDto);
    }

    @Test
    public void whenFindAllDtoThenGetList() {
        when(vacancyRepository.findAll()).thenReturn(List.of(vacancy));
        when(vacancyMapper.toDtoList(List.of(vacancy))).thenReturn(List.of(vacancyDto));
        assertThat(vacancyService.findAllDto()).isEqualTo(List.of(vacancyDto));
    }

    @Test
    public void whenFindAllWithFiltersAndNoApplicableFiltersThenReturnListWithoutFilters() {
        when(filters.get(0).isApplicable(vacancyFilterDto)).thenReturn(false);
        when(vacancyRepository.findAll()).thenReturn(List.of(vacancy));
        when(vacancyMapper.toDtoList(List.of(vacancy))).thenReturn(List.of(vacancyDto));
        List<VacancyDto> filteredVacancies = vacancyService.findAllWithFilter(vacancyFilterDto);
        assertThat(filteredVacancies).isEqualTo(List.of(vacancyDto));
    }

    @Test
    public void whenFindAllWithFiltersThenReturnFilteredList() {
        when(filters.get(0).isApplicable(vacancyFilterDto)).thenReturn(true);
        when(filters.get(0).filter(any(), any())).thenReturn(Stream.empty());
        when(vacancyRepository.findAll()).thenReturn(List.of(vacancy));
        when(vacancyMapper.toDtoList(List.of(vacancy))).thenReturn(List.of(vacancyDto));
        List<VacancyDto> filteredVacancies = vacancyService.findAllWithFilter(vacancyFilterDto);
        assertThat(filteredVacancies).isEmpty();
    }
}