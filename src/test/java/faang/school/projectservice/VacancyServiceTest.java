package faang.school.projectservice;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.filter.VacancyFilter;
import faang.school.projectservice.service.vacancy.VacancyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.doubleThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VacancyServiceTest {

    @InjectMocks
    private VacancyServiceImpl vacancyService;

    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private VacancyMapper vacancyMapper;
    @Mock
    private List<VacancyFilter> vacancyFilters;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testAssignRoleToCurator() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setProjectId(1L );
        vacancyDto.setCreatedBy(1L);

        Vacancy vacancy = new Vacancy();

        Project project = new Project();
        Team team = new Team();
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(1L);
        team.setTeamMembers(List.of(teamMember));
        project.setTeams(List.of(team));

        vacancy.setProject(project);

        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        vacancyService.create(vacancyDto);

        assertEquals(List.of(TeamRole.OWNER), teamMember.getRoles());
    }

    @Test
    public void testValidCuratorRole() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setProjectId(1L );
        vacancyDto.setCreatedBy(10L);

        Vacancy vacancy = new Vacancy();

        Project project = new Project();
        Team team = new Team();
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(1L);
        team.setTeamMembers(List.of(teamMember));
        project.setTeams(List.of(team));

        vacancy.setProject(project);

        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        Throwable exception = assertThrows(DataValidationException.class, () -> vacancyService.create(vacancyDto));
        assertEquals("TeamMember is null", exception.getMessage());
    }

    @Test
    public void testCreate() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setProjectId(1L );
        vacancyDto.setCreatedBy(1L);

        Vacancy vacancy = new Vacancy();
        Project project = new Project();
        Team team = new Team();
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(1L);
        teamMember.setRoles(List.of(TeamRole.OWNER));
        team.setTeamMembers(List.of(teamMember));
        project.setTeams(List.of(team));

        vacancy.setProject(project);

        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        vacancyService.create(vacancyDto);

        verify(vacancyRepository).save(vacancy);
    }

    @Test
    public void testUpdateVacancyExists() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setId(1L);
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);

        when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);

        vacancyService.update(vacancyDto);

        verify(vacancyRepository).save(vacancy);
    }

    @Test
    public void testUpdateVacancyNotExists() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setId(1L);

        when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            vacancyService.update(vacancyDto);
        });
        assertEquals("Vacancies with given id do not exist", exception.getMessage());
    }

    @Test
    public void testValidationRequiredCandidates() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setId(1L);
        vacancyDto.setCandidateIds(List.of(1L));
        vacancyDto.setCount(5);
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setStatus(VacancyStatus.CLOSED);

        when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(new Candidate()));

        Throwable exception = assertThrows(DataValidationException.class, () -> vacancyService.update(vacancyDto));
        assertEquals(vacancyDto.getStatus(), VacancyStatus.OPEN);
        assertEquals("You can't close a vacancy if there are fewer candidates than needed", exception.getMessage());

    }

    @Test
    public void testValidationCandidatesIsNull() {
        VacancyDto vacancyDto = new VacancyDto();
        Vacancy vacancy = new Vacancy();

        candidatePreparation(vacancyDto, vacancy);

        when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(candidateRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(DataValidationException.class, () -> vacancyService.update(vacancyDto));
        assertEquals("Candidate is null", exception.getMessage());
    }

    @Test
    public void testValidationCandidatesIdNull() {
        VacancyDto vacancyDto = new VacancyDto();
        Vacancy vacancy = new Vacancy();

        Candidate candidate = candidatePreparation(vacancyDto, vacancy);

        when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

        Throwable exception = assertThrows(DataValidationException.class, () -> vacancyService.update(vacancyDto));
        assertEquals("User id is null", exception.getMessage());
    }

    @Test
    public void testValidationCandidatesResumeEmpty() {
        VacancyDto vacancyDto = new VacancyDto();
        Vacancy vacancy = new Vacancy();

        Candidate candidate = candidatePreparation(vacancyDto, vacancy);
        candidate.setUserId(1L);

        when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

        Throwable exception = assertThrows(DataValidationException.class, () -> vacancyService.update(vacancyDto));
        assertEquals("Resume is empty", exception.getMessage());
    }

    @Test
    public void testValidationCandidatesStatus() {
        VacancyDto vacancyDto = new VacancyDto();
        Vacancy vacancy = new Vacancy();

        Candidate candidate = candidatePreparation(vacancyDto, vacancy);
        candidate.setUserId(1L);
        candidate.setResumeDocKey("resume");
        candidate.setCandidateStatus(CandidateStatus.ACCEPTED);

        when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

        Throwable exception = assertThrows(DataValidationException.class, () -> vacancyService.update(vacancyDto));
        assertEquals("You have already been reviewed", exception.getMessage());
    }

    @Test
    public void testDelete() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setProjectId(1L);
        Vacancy vacancy = new Vacancy();

        Project project = new Project();
        Team team = new Team();
        List teamMembers = new ArrayList<>();
        team.setTeamMembers(teamMembers);
        project.setTeams(List.of(team));

        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);

        vacancyService.delete(vacancyDto);

        verify(vacancyRepository).delete(vacancy);
        verify(teamRepository, times(1)).save(team);
    }

    @Test
    public void testGetVacancyById() {
        VacancyDto vacancyDto = new VacancyDto();
        Vacancy vacancy = new Vacancy();
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));

        vacancyService.getVacancyById(vacancyDto);

        verify(vacancyRepository).findById(vacancyDto.getId());
        verify(vacancyMapper).toDto(vacancy);
    }

    @Test
    public void testGetVacancyByIdNull() {
        VacancyDto vacancyDto = new VacancyDto();

        when(vacancyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> vacancyService.getVacancyById(vacancyDto));
    }

    private Candidate candidatePreparation(VacancyDto vacancyDto, Vacancy vacancy) {
        vacancyDto.setId(1L);
        vacancyDto.setCandidateIds(List.of(1L));
        vacancyDto.setCount(1);

        vacancy.setId(1L);
        vacancy.setStatus(VacancyStatus.CLOSED);

        Candidate candidate = new Candidate();
        return candidate;
    }


}
