package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.vacancy.Vacancy;
import faang.school.projectservice.model.vacancy.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceImplTest {

    @InjectMocks
    private VacancyServiceImpl vacancyService;

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private TeamMemberJpaRepository teamMemberRepository;

    @Mock
    private VacancyMapper vacancyMapper;

    @Mock
    private VacancyValidator vacancyValidator;

    @Mock
    private List<VacancyFilter> vacancyFilters;

    private final long vacancyId = 1L;
    private VacancyDto vacancyDto;
    private Vacancy vacancy;
    private TeamMember curator;

    @BeforeEach
    void setUp() {
        vacancyDto = VacancyDto.builder()
                .id(1L)
                .name("Junior Developer")
                .description("Looking for junior developers")
                .candidateIds(List.of(101L, 102L, 103L))
                .projectId(1L)
                .createdBy(100L)
                .status("OPEN")
                .salary(50000.0)
                .workSchedule("FULL_TIME")
                .count(5)
                .requiredSkillIds(List.of(1L, 2L))
                .build();

        vacancy = Vacancy.builder()
                .id(1L)
                .name("Junior Developer")
                .description("Looking for junior developers")
                .count(3)
                .status(VacancyStatus.OPEN)
                .build();
    }

    @Test
    void create_shouldValidateProjectAndCuratorAndSaveVacancy() {
        // given
        doNothing().when(vacancyValidator).validateProject(vacancyDto.projectId());
        curator = TeamMember.builder().build();
        when(teamMemberRepository.findById(vacancyDto.createdBy())).thenReturn(Optional.of(curator));
        doNothing().when(vacancyValidator).validateCurator(curator);
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);
        // when
        VacancyDto result = vacancyService.create(vacancyDto);
        // then
        verify(vacancyValidator).validateProject(vacancyDto.projectId());
        verify(vacancyValidator).validateCurator(curator);
        verify(vacancyMapper).toEntity(vacancyDto);
        verify(vacancyRepository).save(vacancy);
        verify(vacancyMapper).toDto(vacancy);
        assertThat(result).isEqualTo(vacancyDto);
    }

    @Test
    void create_shouldSetStatusToOpenBeforeSaving() {
        // given
        doNothing().when(vacancyValidator).validateProject(vacancyDto.projectId());
        curator = TeamMember.builder().build();
        when(teamMemberRepository.findById(vacancyDto.createdBy())).thenReturn(Optional.of(curator));
        doNothing().when(vacancyValidator).validateCurator(curator);
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);
        // when
        vacancyService.create(vacancyDto);
        // then
        assertThat(vacancy.getStatus()).isEqualTo(VacancyStatus.OPEN);
    }

    @Test
    void updateVacancy_whenNotEnoughCandidates_shouldSetStatusToOpen() {
        // given
        vacancyDto = VacancyDto.builder()
                .id(1L)
                .name("Junior Developer Updated")
                .description("Updated description")
                .candidateIds(List.of(101L)) // Недостаточно кандидатов
                .count(5)
                .workSchedule("FULL_TIME")
                .build();
        when(vacancyRepository.findById(vacancyDto.id())).thenReturn(Optional.of(vacancy));
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);
        // when
        VacancyDto result = vacancyService.update(vacancyDto);
        // then
        verify(vacancyValidator, never()).validateCandidates(vacancyDto);
        assertThat(vacancy.getStatus()).isEqualTo(VacancyStatus.OPEN);
        verify(vacancyRepository).save(vacancy);
        assertThat(result).isEqualTo(vacancyDto);
    }

    @Test
    void updateVacancy_whenVacancyNotFound_shouldThrowException() {
        // given
        when(vacancyRepository.findById(vacancyDto.id())).thenReturn(Optional.empty());
        // when/then
        assertThatThrownBy(() -> vacancyService.update(vacancyDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Vacancy with ID: %d not found".formatted(vacancyDto.id()));
        verify(vacancyRepository, never()).save(any(Vacancy.class));
    }

    @Test
    void delete_whenVacancyExists_shouldRemoveCandidatesAndVacancy() {
        // given
        Candidate candidate1 = Candidate.builder()
                .id(101L)
                .candidateStatus(CandidateStatus.REJECTED)
                .build();
        Candidate candidate2 = Candidate.builder()
                .id(102L)
                .candidateStatus(CandidateStatus.ACCEPTED)
                .build();
        vacancy = Vacancy.builder()
                .candidates(List.of(candidate1, candidate2))
                .build();
        when(vacancyRepository.findById(vacancy.getId())).thenReturn(Optional.of(vacancy));
        // when
        vacancyService.delete(vacancy.getId());
        // then
        verify(vacancyRepository).deleteAllById(List.of(candidate1.getId()));
        verify(vacancyRepository, never()).deleteAllById(List.of(candidate2.getId()));
    }

    @Test
    void delete_whenVacancyNotFound_shouldThrowException() {
        // given
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());
        // when/then
        assertThatThrownBy(() -> vacancyService.delete(vacancyId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Vacancy with ID: %d not found".formatted(vacancyId));
        verify(vacancyRepository, never()).deleteById(anyLong());
    }

    @Test
    void findById_whenVacancyExists_shouldReturnVacancyDto() {
        // given
        when(vacancyRepository.findById(vacancy.getId())).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);
        // when
        VacancyDto result = vacancyService.findById(vacancy.getId());
        // then
        verify(vacancyMapper).toDto(vacancy);
        assertThat(result).isEqualTo(vacancyDto);
    }

    @Test
    void findById_whenVacancyNotFound_shouldThrowException() {
        // given
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());
        // when/then
        assertThatThrownBy(() -> vacancyService.findById(vacancyId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Vacancy with ID: %d not found".formatted(vacancyId));
        verify(vacancyMapper, never()).toDto(any(Vacancy.class));
    }

    @Test
    void findAll_whenNoFiltersApplicable_shouldReturnVacancies() {
        // given
        Vacancy vacancy1 = Vacancy.builder().id(1L).name("Java Developer").build();
        Vacancy vacancy2 = Vacancy.builder().id(2L).name("Python Developer").build();
        List<Vacancy> vacancies = List.of(vacancy1, vacancy2);
        when(vacancyRepository.findAll()).thenReturn(vacancies);
        when(vacancyFilters.stream()).thenReturn(Stream.of());
        when(vacancyMapper.toDto(vacancy1)).thenReturn(VacancyDto.builder().id(1L).name("Java Developer").build());
        when(vacancyMapper.toDto(vacancy2)).thenReturn(VacancyDto.builder().id(2L).name("Python Developer").build());
        // when
        List<VacancyDto> result = vacancyService.findAll(new VacancyFilterDto(null, null));
        // then
        verify(vacancyMapper).toDto(vacancy1);
        verify(vacancyMapper).toDto(vacancy2);
        assertThat(result).hasSize(2);
        assertThat(result).extracting(VacancyDto::id).containsExactly(1L, 2L);
    }

    @Test
    void findAll_whenNoVacanciesFound_shouldReturnEmptyList() {
        // given
        when(vacancyRepository.findAll()).thenReturn(List.of());
        when(vacancyFilters.stream()).thenReturn(Stream.of());
        // when
        List<VacancyDto> result = vacancyService.findAll(new VacancyFilterDto(null, null));
        // then
        assertThat(result).isEmpty();
    }
}