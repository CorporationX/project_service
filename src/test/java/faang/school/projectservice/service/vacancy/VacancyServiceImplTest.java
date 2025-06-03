package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.dto.vacancy.CloseVacancyDto;
import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceImplTest {
    private final Long userId = 1L;
    private final Long projectId = 1L;
    private final Long vacancyId = 1L;

    private Vacancy testVacancy;
    private CreateVacancyDto createDto;
    private UpdateVacancyDto updateDto;
    private VacancyFilterDto filterDto;

    @Mock
    private VacancyRepository vacancyRepo;
    @Mock
    private CandidateRepository candidateRepo;
    @Mock
    private TeamMemberRepository teamMemberRepo;
    @Mock
    private UserContext userContext;
    @Mock
    private VacancyMapper vacancyMapper;
    @Mock
    private VacancyValidator validator;
    @Mock
    private Filter<Vacancy, VacancyFilterDto> mockFilter;
    @InjectMocks
    private VacancyServiceImpl vacancyService;

    @BeforeEach
    void setUp() {
        createDto = CreateVacancyDto.builder()
                .projectId(projectId)
                .position(TeamRole.DEVELOPER)
                .count(3)
                .name("Java Developer")
                .build();

        updateDto = UpdateVacancyDto.builder()
                .id(vacancyId)
                .count(5)
                .candidatesToAdd(List.of(
                        CandidateDto.builder().userId(101L).username("user101").build(),
                        CandidateDto.builder().userId(102L).username("user102").build()
                ))
                .build();

        filterDto = new VacancyFilterDto("Java", "DEVELOPER");

        testVacancy = Vacancy.builder()
                .id(vacancyId)
                .project(Project.builder().id(projectId).build())
                .count(3)
                .status(VacancyStatus.OPEN)
                .candidates(new ArrayList<>(List.of(
                        Candidate.builder()
                                .id(101L)
                                .candidateStatus(CandidateStatus.WAITING_RESPONSE)
                                .build()
                )))
                .build();
        vacancyService = new VacancyServiceImpl(
                vacancyRepo,
                candidateRepo,
                userContext,
                teamMemberRepo,
                vacancyMapper,
                validator,
                List.of(mockFilter)
        );
    }

    @Test
    @DisplayName("Создание вакансии - проверка всех зависимостей")
    void shouldCallAllDependencies_whenCreateVacancy() {
        VacancyResponseDto responseDto = new VacancyResponseDto();

        when(userContext.getUserId()).thenReturn(userId);
        when(vacancyMapper.toVacancyEntity(createDto)).thenReturn(testVacancy);
        when(vacancyRepo.save(testVacancy)).thenReturn(testVacancy);
        when(vacancyMapper.toVacancyDto(testVacancy)).thenReturn(responseDto);

        VacancyResponseDto result = vacancyService.createVacancy(createDto);

        verify(vacancyMapper).toVacancyEntity(createDto);
        verify(vacancyRepo).save(testVacancy);
        verify(vacancyMapper).toVacancyDto(testVacancy);
        assertThat(result).isEqualTo(responseDto);
        assertThat(testVacancy.getStatus()).isEqualTo(VacancyStatus.OPEN);
        assertThat(testVacancy.getCreatedBy()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Обновление вакансии - полная проверка")
    void shouldUpdateAllFieldsAndCandidates_whenUpdateVacancy() {
        when(vacancyRepo.findById(vacancyId)).thenReturn(Optional.of(testVacancy));
        when(userContext.getUserId()).thenReturn(userId);

        vacancyService.updateVacancy(updateDto);

        verify(vacancyRepo).findById(vacancyId);
        assertThat(testVacancy.getCount()).isEqualTo(3);
        assertThat(testVacancy.getUpdatedBy()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Закрытие вакансии с правильным количеством кандидатов")
    void testCloseVacancy_whenValidCandidates_thenClosesVacancy() {
        CloseVacancyDto dto = new CloseVacancyDto(List.of(101L));
        testVacancy.setCount(1);

        when(vacancyRepo.findById(vacancyId)).thenReturn(Optional.of(testVacancy));

        vacancyService.closeVacancy(vacancyId, dto);

        assertThat(testVacancy.getStatus()).isEqualTo(VacancyStatus.CLOSED);
        assertThat(testVacancy.getCandidates().get(0).getCandidateStatus())
                .isEqualTo(CandidateStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Получение вакансии по существующему ID")
    void testGetVacancyById_whenExists_thenReturnsVacancyDto() {
        VacancyResponseDto expectedDto = new VacancyResponseDto();

        when(vacancyRepo.findById(vacancyId)).thenReturn(Optional.of(testVacancy));
        when(vacancyMapper.toVacancyDto(testVacancy)).thenReturn(expectedDto);

        VacancyResponseDto result = vacancyService.getVacancyById(vacancyId);

        assertThat(result).isEqualTo(expectedDto);
        verify(vacancyRepo).findById(vacancyId);
    }

    @Test
    @DisplayName("Получение отфильтрованных вакансий")
    void testGetFilteredVacancies_whenFiltersApplied_thenReturnsFiltered() {
        Vacancy matchingVacancy = Vacancy.builder()
                .id(2L)
                .name("Java Engineer")
                .position(TeamRole.DEVELOPER)
                .build();

        VacancyResponseDto expectedDto = new VacancyResponseDto();

        when(vacancyRepo.findAll()).thenReturn(List.of(testVacancy, matchingVacancy));
        when(mockFilter.isApplicable(filterDto)).thenReturn(true);
        when(mockFilter.apply(any(), eq(filterDto))).thenReturn(Stream.of(matchingVacancy));
        when(vacancyMapper.toVacancyDto(matchingVacancy)).thenReturn(expectedDto);

        List<VacancyResponseDto> result = vacancyService.getFilteredVacancies(filterDto);

        verify(vacancyRepo).findAll();
        verify(mockFilter).isApplicable(filterDto);
        verify(mockFilter).apply(any(), eq(filterDto));

        verify(vacancyMapper, times(1)).toVacancyDto(matchingVacancy);
        verify(vacancyMapper, never()).toVacancyDto(testVacancy);
        assertThat(result).hasSize(1).containsExactly(expectedDto);
    }
}
