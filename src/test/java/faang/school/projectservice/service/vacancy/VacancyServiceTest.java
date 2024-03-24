package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.vacancy.filter.VacancyFilter;
import faang.school.projectservice.validation.vacancy.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    private VacancyRepository vacancyRepository;
    private VacancyMapper vacancyMapper;
    private VacancyValidator vacancyValidator;
    private TeamMemberJpaRepository teamMemberJpaRepository;
    private List<VacancyFilter> vacancyFilters;
    private VacancyService vacancyService;

    private Vacancy vacancy;
    private VacancyDto vacancyDto;
    private Candidate candidate;
    private Project project;
    private TeamMember curator;
    private VacancyFilterDto vacancyFilterDto;
    private VacancyFilter vacancyFilter;

    @BeforeEach
    void setUp() {
        curator = TeamMember.builder()
                .id(1L)
                .userId(6L)
                .roles(List.of(TeamRole.MANAGER))
                .build();
        candidate = Candidate.builder()
                .id(15L)
                .userId(15L)
                .build();
        project = Project.builder()
                .id(3L)
                .name("Valid name")
                .description("Valid Description")
                .build();
        vacancy = Vacancy.builder()
                .id(4L)
                .name("Valid name")
                .description("Valid Description")
                .project(project)
                .createdBy(curator.getId())
                .count(2)
                .build();
        vacancyDto = VacancyDto.builder()
                .id(vacancy.getId())
                .name(vacancy.getName())
                .description(vacancy.getDescription())
                .projectId(vacancy.getProject().getId())
                .curatorId(vacancy.getCreatedBy())
                .workersRequired(vacancy.getCount())
                .build();
        vacancyFilterDto = VacancyFilterDto.builder()
                .name("name")
                .status(VacancyStatus.OPEN)
                .build();

        vacancyRepository = mock(VacancyRepository.class);
        vacancyMapper = mock(VacancyMapper.class);
        vacancyValidator = mock(VacancyValidator.class);
        teamMemberJpaRepository = mock(TeamMemberJpaRepository.class);
        vacancyFilter = mock(VacancyFilter.class);
        vacancyFilters = List.of(vacancyFilter);

        vacancyService = new VacancyService(vacancyRepository, vacancyMapper, vacancyValidator, teamMemberJpaRepository,
                vacancyFilters);
    }

    @Test
    void create_VacancyCreatedAndSaved_ThenReturnedAsDto() {
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);
        when(vacancyMapper.toDto(any(Vacancy.class))).thenReturn(vacancyDto);
        when(vacancyMapper.toEntity(any(VacancyDto.class))).thenReturn(vacancy);

        vacancyService.create(vacancyDto);

        assertEquals(VacancyStatus.OPEN, vacancyDto.getStatus());
        verify(vacancyRepository, times(1)).save(vacancy);
        verify(vacancyMapper, times(1)).toDto(vacancy);
    }

    @Test
    void update_OpenVacancyUpdatedAndSaved_ThenReturnedAsDto() {
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);
        when(vacancyMapper.toEntity(any(VacancyDto.class))).thenReturn(vacancy);
        when(vacancyMapper.toDto(any(Vacancy.class))).thenReturn(vacancyDto);
        vacancyDto.setStatus(VacancyStatus.OPEN);

        vacancyService.update(vacancyDto);

        verify(vacancyMapper, times(1)).toEntity(vacancyDto);
        verify(vacancyRepository, times(1)).save(vacancy);
        verify(vacancyMapper, times(1)).toDto(vacancy);
        verify(vacancyValidator, never()).validateIfCandidatesNoMoreNeeded(vacancy);
        verify(vacancyValidator, never()).validateIfVacancyCanBeClosed(vacancyDto);
    }

    @Test
    void update_ClosedVacancyUpdatedAndSaved_ThenReturnedAsDto() {
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.ofNullable(vacancy));
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);
        when(vacancyMapper.toEntity(any(VacancyDto.class))).thenReturn(vacancy);
        when(vacancyMapper.toDto(any(Vacancy.class))).thenReturn(vacancyDto);
        vacancyDto.setStatus(VacancyStatus.CLOSED);

        vacancyService.update(vacancyDto);

        verify(vacancyRepository, times(1)).findById(vacancyDto.getId());
        verify(vacancyMapper, times(1)).toEntity(vacancyDto);
        verify(vacancyRepository, times(1)).save(vacancy);
        verify(vacancyMapper, times(1)).toDto(vacancy);
        verify(vacancyValidator, times(1)).validateIfCandidatesNoMoreNeeded(vacancy);
        verify(vacancyValidator, times(1)).validateIfVacancyCanBeClosed(vacancyDto);
    }

    @Test
    void delete_VacancyDeletedTeamMemberHasNoRole_TeamMemberAlsoDeleted() {
        TeamMember teamMember = TeamMember.builder()
                .id(10L)
                .userId(15L)
                .build();
        Team team = Team.builder()
                .id(20L)
                .teamMembers(List.of(teamMember))
                .build();
        project.setTeams(List.of(team));
        vacancy.setCandidates(List.of(candidate));
        when(vacancyRepository.findById(vacancy.getId())).thenReturn(Optional.ofNullable(vacancy));

        vacancyService.delete(vacancy.getId());

        verify(vacancyRepository, times(1)).findById(vacancy.getId());
        verify(teamMemberJpaRepository, times(1)).deleteAll(List.of(teamMember));
        verify(vacancyRepository, times(1)).delete(vacancy);
    }

    @Test
    void delete_VacancyDeletedTeamMemberHasRole_TeamMemberNotDeleted() {
        TeamRole role = TeamRole.DEVELOPER;
        TeamMember teamMember = TeamMember.builder()
                .id(10L)
                .userId(15L)
                .roles(List.of(role))
                .build();
        Team team = Team.builder()
                .id(20L)
                .teamMembers(List.of(teamMember))
                .build();
        project.setTeams(List.of(team));
        vacancy.setCandidates(List.of(candidate));
        when(vacancyRepository.findById(vacancy.getId())).thenReturn(Optional.ofNullable(vacancy));

        vacancyService.delete(vacancy.getId());

        verify(vacancyRepository, times(1)).findById(vacancy.getId());
        verify(teamMemberJpaRepository, times(1)).deleteAll(Collections.emptyList());
        verify(vacancyRepository, times(1)).delete(vacancy);
    }

    @Test
    void delete_VacancyDeletedTeamMemberIsNotCandidate_TeamMemberNotDeleted() {
        TeamMember teamMember = TeamMember.builder()
                .id(10L)
                .userId(77L)
                .build();
        Team team = Team.builder()
                .id(20L)
                .teamMembers(List.of(teamMember))
                .build();
        project.setTeams(List.of(team));
        vacancy.setCandidates(List.of(candidate));
        when(vacancyRepository.findById(vacancy.getId())).thenReturn(Optional.ofNullable(vacancy));

        vacancyService.delete(vacancy.getId());

        verify(vacancyRepository, times(1)).findById(vacancy.getId());
        verify(teamMemberJpaRepository, times(1)).deleteAll(Collections.emptyList());
        verify(vacancyRepository, times(1)).delete(vacancy);
    }

    @Test
    void getFilteredVacancies_VacanciesFiltered_ThenReturnedAsDto() {
        List<Vacancy> vacancies = List.of(vacancy);

        when(vacancyRepository.findAll()).thenReturn(vacancies);
        when(vacancyFilter.isApplicable(vacancyFilterDto)).thenReturn(true);
        when(vacancyMapper.toDto(vacancies)).thenReturn(List.of(vacancyDto));

        vacancyService.getFilteredVacancies(vacancyFilterDto);

        verify(vacancyRepository, times(1)).findAll();
        verify(vacancyFilter, times(1)).isApplicable(vacancyFilterDto);
        verify(vacancyFilter, times(1)).apply(vacancies, vacancyFilterDto);
        verify(vacancyMapper, times(1)).toDto(vacancies);
    }

    @Test
    void getVacancyById_VacancyFoundById_ThenReturnedAsDto() {
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.ofNullable(vacancy));
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        vacancyService.getVacancyById(4L);

        verify(vacancyRepository, times(1)).findById(anyLong());
        verify(vacancyMapper, times(1)).toDto(vacancy);
        assertDoesNotThrow(() -> vacancyService.getVacancyById(4L));
    }

    @Test
    void getVacancyById_VacancyNotFoundById_ShouldThrowEntityNotFoundException() {
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                vacancyService.getVacancyById(5L));
        verify(vacancyRepository, times(1)).findById(anyLong());
        verify(vacancyMapper, never()).toDto(any(Vacancy.class));
    }
}
