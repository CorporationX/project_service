package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.filter.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.filter.VacancyFilter;
import faang.school.projectservice.validator.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    public static final Long PROJECT_ID = 1L;
    public static final String VACANCY_NAME = "Java Developer";
    public static final String VACANCY_DESCRIPTION = "Develops Java applications";
    private static final Long CANDIDATE_ID = 1L;
    private static final Integer CANDIDATE_COUNT = 5;
    private static final Long CURATOR_ID = 1L;
    public static final Long VACANCY_ID = 1L;
    public static final Long SECOND_VACANCY_ID = 2L;
    public static final Long TEAM_MEMBER_ID = 1L;

    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private VacancyValidator vacancyValidator;
    @Mock
    private VacancyMapper vacancyMapper;
    @Mock
    private List<VacancyFilter> vacancyFilters;
    @Mock
    private VacancyFilter filter;

    @InjectMocks
    private VacancyService vacancyService;

    private VacancyDto vacancyDto;
    private Vacancy vacancy;
    private Vacancy secondVacancy;
    private Project project;
    private Candidate candidate;
    private TeamMember teamMember;
    private List<Vacancy> allVacancies;
    private List<VacancyDto> vacancyDtoList;
    private VacancyFilterDto filterDto;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(PROJECT_ID)
                .build();
        candidate = new Candidate();
        candidate.setId(CANDIDATE_ID);
        candidate.setUserId(CANDIDATE_ID);
        vacancyDto = VacancyDto.builder()
                .id(VACANCY_ID)
                .name(VACANCY_NAME)
                .description(VACANCY_DESCRIPTION)
                .projectId(PROJECT_ID)
                .candidateIds(List.of(CANDIDATE_ID))
                .createdBy(CURATOR_ID)
                .status(VacancyStatus.OPEN)
                .count(CANDIDATE_COUNT)
                .build();
        vacancy = Vacancy.builder()
                .id(VACANCY_ID)
                .name(VACANCY_NAME)
                .description(VACANCY_DESCRIPTION)
                .project(project)
                .candidates(List.of(candidate))
                .createdBy(CURATOR_ID)
                .status(VacancyStatus.OPEN)
                .count(CANDIDATE_COUNT)
                .build();
        secondVacancy = Vacancy.builder()
                .id(SECOND_VACANCY_ID)
                .name(VACANCY_NAME)
                .description(VACANCY_DESCRIPTION)
                .project(project)
                .candidates(List.of(candidate))
                .createdBy(CURATOR_ID)
                .status(VacancyStatus.OPEN)
                .count(CANDIDATE_COUNT)
                .build();
        teamMember = TeamMember.builder()
                .id(TEAM_MEMBER_ID)
                .userId(CANDIDATE_ID)
                .roles(List.of(TeamRole.DEVELOPER))
                .build();
        allVacancies = List.of(vacancy);

        vacancyDtoList = List.of(vacancyDto);
        filterDto = VacancyFilterDto.builder()
                .namePattern("Java developer")
                .teamRole(TeamRole.DEVELOPER)
                .build();
    }

    @Test
    @DisplayName("Create a vacancy when everything is fine")
    void testCreateSuccess() {
        when(vacancyValidator.validate(vacancyDto)).thenReturn(project);
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.create(vacancyDto);

        assertThat(result).isEqualTo(vacancyDto);
        assertThat(result.getStatus()).isEqualTo(VacancyStatus.OPEN);
        assertThat(result.getProjectId()).isEqualTo(PROJECT_ID);

        verify(vacancyValidator).validate(vacancyDto);
        verify(vacancyMapper).toEntity(vacancyDto);
        verify(vacancyRepository).save(vacancy);
        verify(vacancyMapper).toDto(vacancy);
    }

    @Test
    @DisplayName("Create a vacancy when validation error")
    void testCreateWithValidationException() {
        when(vacancyValidator.validate(vacancyDto)).thenThrow(new DataValidationException("Validation failed"));

        assertThatThrownBy(() -> vacancyService.create(vacancyDto)).isInstanceOf(DataValidationException.class)
                .hasMessage("Validation failed");

        verify(vacancyValidator).validate(vacancyDto);
        verify(vacancyMapper, never()).toEntity(any());
        verify(vacancyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create a vacancy when repository error")
    void testCreateWithRepositoryException() {
        when(vacancyValidator.validate(vacancyDto)).thenReturn(project);
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(vacancyRepository.save(vacancy)).thenThrow(new RuntimeException("Repository error"));

        assertThatThrownBy(() -> vacancyService.create(vacancyDto)).isInstanceOf(RuntimeException.class)
                .hasMessage("Repository error");

        verify(vacancyValidator).validate(vacancyDto);
        verify(vacancyMapper).toEntity(vacancyDto);
        verify(vacancyRepository).save(vacancy);
    }

    @Test
    @DisplayName("Update a vacancy when everything is fine")
    void testUpdateSuccess() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(candidate));
        when(teamMemberRepository.findByUserIdAndProjectId(CANDIDATE_ID, PROJECT_ID)).thenReturn(teamMember);
        when(teamMemberRepository.save(teamMember)).thenReturn(teamMember);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.update(vacancyDto);

        assertThat(result).isEqualTo(vacancyDto);
        assertThat(vacancy.getStatus()).isEqualTo(VacancyStatus.CLOSED);

        verify(vacancyValidator).validateVacancyStatus(vacancyDto);
        verify(vacancyValidator).validate(vacancyDto);
        verify(vacancyValidator).validateCandidatesCount(any(), eq(vacancyDto));
        verify(teamMemberRepository, times(1)).save(any(TeamMember.class));
        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(any(), any());
        verify(vacancyRepository, times(1)).save(any(Vacancy.class));
        verify(vacancyMapper, times(1)).toDto(any(Vacancy.class));
    }

    @Test
    @DisplayName("Update a vacancy when vacancy is not found")
    void testUpdateVacancyNotFound() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vacancyService.update(vacancyDto)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Vacancy not found!");

        verify(vacancyRepository, times(1)).findById(VACANCY_ID);
        verifyNoMoreInteractions(vacancyRepository);
        verifyNoInteractions(candidateRepository);
        verifyNoInteractions(teamMemberRepository);
        verifyNoInteractions(vacancyMapper);
    }

    @Test
    @DisplayName("Update a vacancy when candidate is not found")
    void testUpdateCandidateNotFound() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vacancyService.update(vacancyDto)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Candidate doesn't exist by id: 1");

        verify(vacancyRepository, times(1)).findById(VACANCY_ID);
        verify(candidateRepository, times(1)).findById(CANDIDATE_ID);
        verifyNoMoreInteractions(vacancyRepository);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(teamMemberRepository);
        verifyNoInteractions(vacancyMapper);
    }

    @Test
    @DisplayName("Update a vacancy when team member is not found")
    void testUpdateTeamMemberNotFound() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(candidate));
        doThrow(new EntityNotFoundException("Validation failed")).when(teamMemberRepository).findByUserIdAndProjectId(any(), any());

        assertThatThrownBy(() -> vacancyService.update(vacancyDto)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Validation failed");

        verify(vacancyRepository, times(1)).findById(VACANCY_ID);
        verify(candidateRepository, times(1)).findById(CANDIDATE_ID);
        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(CANDIDATE_ID, PROJECT_ID);
        verifyNoMoreInteractions(vacancyRepository);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(vacancyMapper);
    }

    @Test
    @DisplayName("Update a vacancy when validation fails")
    void testUpdateValidationFails() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        doThrow(new ValidationException("Validation failed")).when(vacancyValidator).validate(any(VacancyDto.class));

        assertThatThrownBy(() -> vacancyService.update(vacancyDto)).isInstanceOf(ValidationException.class)
                .hasMessage("Validation failed");

        verify(vacancyRepository, times(1)).findById(VACANCY_ID);
        verify(vacancyValidator, times(1)).validate(vacancyDto);
        verifyNoMoreInteractions(vacancyRepository);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoMoreInteractions(teamMemberRepository);
        verifyNoInteractions(vacancyMapper);
    }

    @Test
    @DisplayName("Delete vacancy when everything is fine")
    void testDeleteSuccess() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(teamMemberRepository.findByUserIdAndProjectId(CANDIDATE_ID, PROJECT_ID)).thenReturn(teamMember);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.delete(VACANCY_ID);

        assertThat(result).isNotNull();
        assertThat(VACANCY_ID).isEqualTo(result.getId());

        verify(vacancyRepository).findById(VACANCY_ID);
        verify(teamMemberRepository).findByUserIdAndProjectId(CANDIDATE_ID, PROJECT_ID);
        verify(vacancyRepository).deleteById(VACANCY_ID);
        verify(vacancyMapper).toDto(vacancy);
    }

    @Test
    @DisplayName("Delete vacancy when vacancy not found")
    void testDeleteVacancyNotFound() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vacancyService.delete(VACANCY_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Vacancy not found!");

        verify(vacancyRepository).findById(VACANCY_ID);
        verify(vacancyRepository, never()).deleteById(VACANCY_ID);
    }

    @Test
    @DisplayName("Get all vacancies by filter when everything is fine")
    void testGetAllByFilterSuccess() {
        when(vacancyRepository.findAll()).thenReturn(allVacancies);
        when(vacancyFilters.stream()).thenReturn(Stream.of(filter));
        when(filter.isApplicable(filterDto)).thenReturn(true);
        when(filter.apply(any(Supplier.class), eq(filterDto))).thenAnswer(invocation -> {
            Supplier<Stream<Vacancy>> supplier = invocation.getArgument(0);
            return supplier.get();
        });
        when(vacancyMapper.toDtoList(anyList())).thenReturn(vacancyDtoList);

        List<VacancyDto> result = vacancyService.getAllByFilter(filterDto);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).isEqualTo(vacancyDtoList);

        verify(vacancyRepository).findAll();
        verify(filter).isApplicable(filterDto);
        verify(filter).apply(any(Supplier.class), eq(filterDto));
        verify(vacancyMapper).toDtoList(allVacancies);
    }

    @Test
    @DisplayName("Get vacancy when everything is fine")
    void testGetSuccess() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.get(VACANCY_ID);
        assertThat(result).isEqualTo(vacancyDto);

        verify(vacancyRepository).findById(VACANCY_ID);
        verify(vacancyMapper).toDto(vacancy);
    }

    @Test
    @DisplayName("Get vacancy when vacancy not founded")
    void testGetWhenVacancyNotFound() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vacancyService.get(VACANCY_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Vacancy not found!");

        verify(vacancyRepository).findById(VACANCY_ID);
    }
}