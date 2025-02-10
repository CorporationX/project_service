package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.service.filter.VacancyFilter;
import faang.school.projectservice.mapper.CandidateMapper;
import faang.school.projectservice.mapper.VacancyMapperImpl;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.impl.VacancyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceImplTest {
    @Mock
    private VacancyRepository vacancyRepository;
    @Spy
    private VacancyMapperImpl vacancyMapper;
    @Spy
    private CandidateMapper candidateMapper;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private CandidateRepository candidateRepository;

    @Captor
    private ArgumentCaptor<Vacancy> captor;
    private VacancyServiceImpl vacancyService;

    @BeforeEach
    void setUp() {
        List<VacancyFilter> filters = TestData.createVacancyFilters();

        vacancyService = new VacancyServiceImpl(
                vacancyRepository,
                vacancyMapper,
                projectRepository,
                teamMemberRepository,
                candidateRepository,
                filters);
    }

    @Test
    public void testGetVacanciesByFilterIfAllFiltersSuccess() {
        Vacancy vacancy1 = TestData.createVacancy(1L, "test vacancy", TeamRole.DEVELOPER);
        Vacancy vacancy2 = TestData.createVacancy(2L, "some vacancy", TeamRole.DEVELOPER);
        Vacancy vacancy3 = TestData.createVacancy(3L, "test vacancy", TeamRole.ANALYST);
        Mockito.when(vacancyRepository.findAll()).thenReturn(List.of(vacancy1, vacancy2, vacancy3));

        VacancyFilterDto filter = TestData.createVacancyFilterDto("test", TeamRole.DEVELOPER);

        List<VacancyDto> filteredVacancies = vacancyService.getVacanciesByFilter(filter);

        assertEquals(1, filteredVacancies.size());
        assertTrue(filteredVacancies.get(0).name().contains(filter.nameContains()));
        assertEquals(filteredVacancies.get(0).position(), filter.position());
    }

    @Test
    public void testGetVacancySuccess() {
        Vacancy vacancy = TestData.createVacancy(1L, "test vacancy", TeamRole.DEVELOPER);
        long id = vacancy.getId();
        Mockito.when(vacancyRepository.findById(id)).thenReturn(Optional.of(vacancy));

        VacancyDto dto = vacancyService.getVacancy(id);

        Mockito.verify(vacancyRepository, Mockito.times(1)).findById(id);
        assertEquals(vacancy.getName(), dto.name());
    }

    @Test
    public void testGetVacancyIfNoVacancyExistsFailed() {
        long id = 1L;
        Mockito.when(vacancyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vacancyService.getVacancy(id));
    }

    @Test
    public void testCreateSuccess() {
        vacancyMapper.setCandidateMapper(candidateMapper);
        VacancyRequestDto dto = TestData.createVacancyRequestDto("test vacancy", TeamRole.DEVELOPER, 1L, null,
                1L, null, VacancyStatus.OPEN, 2);
        TeamMember member = new TeamMember();
        member.setRoles(List.of(TeamRole.MANAGER));
        Mockito.when(teamMemberRepository.findByUserIdAndProjectId(dto.createdBy(), dto.projectId())).thenReturn(member);
        Mockito.when(projectRepository.findById(dto.projectId())).thenReturn(Optional.of(new Project()));

        vacancyService.createVacancy(dto);

        Mockito.verify(vacancyRepository, Mockito.times(1)).save(captor.capture());
        Vacancy capturedVacancy = captor.getValue();
        assertEquals(dto.name(), capturedVacancy.getName());
        assertEquals(dto.position(), capturedVacancy.getPosition());
    }

    @Test
    public void testCreateIfRequestedUserDoesNotHaveRequiredRolesFailed() {
        vacancyMapper.setCandidateMapper(candidateMapper);
        VacancyRequestDto dto = TestData.createVacancyRequestDto("test vacancy", TeamRole.DEVELOPER, 1L, null,
                1L, null, VacancyStatus.OPEN, 2);
        TeamMember member = new TeamMember();
        Mockito.when(teamMemberRepository.findByUserIdAndProjectId(dto.createdBy(), dto.projectId())).thenReturn(member);

        assertThrows(DataValidationException.class, () -> vacancyService.createVacancy(dto));
    }

    @Test
    public void testCreateIfProjectNotFoundFailed() {
        vacancyMapper.setCandidateMapper(candidateMapper);
        VacancyRequestDto dto = TestData.createVacancyRequestDto("test vacancy", TeamRole.DEVELOPER, 1L, null,
                1L, null, VacancyStatus.OPEN, 2);
        TeamMember member = new TeamMember();
        member.setRoles(List.of(TeamRole.MANAGER));
        Mockito.when(teamMemberRepository.findByUserIdAndProjectId(dto.createdBy(), dto.projectId())).thenReturn(member);
        Mockito.when(projectRepository.findById(dto.projectId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vacancyService.createVacancy(dto));
    }

    @Test
    public void testUpdateSuccess() {
        vacancyMapper.setCandidateMapper(candidateMapper);
        Vacancy vacancy = TestData.createVacancy(1L, "test vacancy", TeamRole.DEVELOPER);
        VacancyRequestDto dto = TestData.createVacancyRequestDto("test vacancy", TeamRole.DEVELOPER, 1L, null,
                1L, 1L, VacancyStatus.OPEN, 2);
        TeamMember member = new TeamMember();
        member.setRoles(List.of(TeamRole.MANAGER));
        Mockito.when(teamMemberRepository.findByUserIdAndProjectId(dto.createdBy(), dto.projectId())).thenReturn(member);
        Mockito.when(vacancyRepository.findById(vacancy.getId())).thenReturn(Optional.of(vacancy));
        Mockito.when(projectRepository.findById(dto.projectId())).thenReturn(Optional.of(new Project()));

        vacancyService.updateVacancy(dto, vacancy.getId());

        Mockito.verify(vacancyRepository, Mockito.times(1)).save(captor.capture());
        Vacancy capturedVacancy = captor.getValue();
        assertEquals(dto.name(), capturedVacancy.getName());
    }

    @Test
    public void testUpdateIfUpdatedByNullFailed() {
        vacancyMapper.setCandidateMapper(candidateMapper);
        Vacancy vacancy = TestData.createVacancy(1L, "test vacancy", TeamRole.DEVELOPER);
        VacancyRequestDto dto = TestData.createVacancyRequestDto("test vacancy", TeamRole.DEVELOPER, 1L, null,
                1L, null, VacancyStatus.OPEN, 2);

        assertThrows(DataValidationException.class, () -> vacancyService.updateVacancy(dto, vacancy.getId()));
    }

    @Test
    public void testUpdateIfClosedButCandidatesCountNotMatchFailed() {
        vacancyMapper.setCandidateMapper(candidateMapper);
        Vacancy vacancy = TestData.createVacancy(1L, "test vacancy", TeamRole.DEVELOPER);
        VacancyRequestDto dto = TestData.createVacancyRequestDto("test vacancy", TeamRole.DEVELOPER, 1L,
                List.of(), 1L, 1L, VacancyStatus.CLOSED, 1);
        TeamMember member = new TeamMember();
        member.setRoles(List.of(TeamRole.MANAGER));
        Mockito.when(teamMemberRepository.findByUserIdAndProjectId(dto.createdBy(), dto.projectId())).thenReturn(member);
        Mockito.when(vacancyRepository.findById(vacancy.getId())).thenReturn(Optional.of(vacancy));
        Mockito.when(projectRepository.findById(dto.projectId())).thenReturn(Optional.of(new Project()));

        assertThrows(DataValidationException.class, () -> vacancyService.updateVacancy(dto, vacancy.getId()));
    }

    @Test
    public void testUpdateIfClosedButCandidatesDoNotHaveAssignedRoleFailed() {
        vacancyMapper.setCandidateMapper(candidateMapper);
        Vacancy vacancy = TestData.createVacancy(1L, "test vacancy", TeamRole.DEVELOPER);
        VacancyRequestDto dto = TestData.createVacancyRequestDto("test vacancy", TeamRole.DEVELOPER, 1L,
                List.of(1L), 1L, 1L, VacancyStatus.CLOSED, 1);
        TeamMember memberManager = new TeamMember();
        memberManager.setRoles(List.of(TeamRole.MANAGER));

        Project project = new Project();
        project.setId(dto.projectId());
        Candidate candidate = new Candidate();
        candidate.setUserId(5L);
        TeamMember memberCandidate = new TeamMember();
        memberCandidate.setRoles(List.of(TeamRole.INTERN));

        Mockito.when(teamMemberRepository.findByUserIdAndProjectId(dto.createdBy(), dto.projectId()))
                .thenReturn(memberManager);
        Mockito.when(vacancyRepository.findById(vacancy.getId())).thenReturn(Optional.of(vacancy));
        Mockito.when(projectRepository.findById(dto.projectId())).thenReturn(Optional.of(project));
        Mockito.when(candidateRepository.findAllById(dto.candidatesIds())).thenReturn(List.of(candidate));
        Mockito.when(teamMemberRepository.findByUserIdAndProjectId(candidate.getUserId(), dto.projectId()))
                .thenReturn(memberCandidate);

        assertThrows(DataValidationException.class, () -> vacancyService.updateVacancy(dto, vacancy.getId()));
    }

    @Test
    public void testDeleteVacancySuccess() {
        Vacancy vacancy = TestData.createVacancy(1L, "test vacancy", TeamRole.DEVELOPER);
        vacancy.setCandidates(new ArrayList<>(List.of(new Candidate())));
        Mockito.when(vacancyRepository.findById(vacancy.getId())).thenReturn(Optional.of(vacancy));

        vacancyService.deleteVacancy(vacancy.getId());
        assertEquals(0, vacancy.getCandidates().size());
        Mockito.verify(vacancyRepository, Mockito.times(1)).deleteById(vacancy.getId());
    }
}
