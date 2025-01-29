package faang.school.projectservice.service;

import faang.school.projectservice.adapter.CandidateRepositoryAdapter;
import faang.school.projectservice.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.adapter.TeamMemberRepositoryAdapter;
import faang.school.projectservice.adapter.VacancyRepositoryAdapter;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.VacancyDTO;
import faang.school.projectservice.exception.BadRequestException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @InjectMocks
    private VacancyService vacancyService;
    @Mock
    private VacancyMapper vacancyMapper;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private ProjectRepositoryAdapter projectRepositoryAdapter;
    @Mock
    private TeamMemberRepositoryAdapter teamMemberRepositoryAdapter;
    @Mock
    private UserContext userContext;
    @Mock
    private VacancyRepositoryAdapter vacancyRepositoryAdapter;
    @Mock
    private CandidateRepositoryAdapter candidateRepositoryAdapter;

    @Test
    void testCreateVacancy_Success() {
        VacancyDTO vacancyDTO = new VacancyDTO();
        vacancyDTO.setProjectId(1L);
        vacancyDTO.setStatus(VacancyStatus.OPEN);

        Project project = new Project();
        project.setId(1L);

        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.OWNER));

        Vacancy vacancy = new Vacancy();
        Mockito.when(projectRepositoryAdapter.getById(1L)).thenReturn(project);
        Mockito.when(userContext.getUserId()).thenReturn(1L);
        Mockito.when(teamMemberRepositoryAdapter.getByUserIdAndProjectId(1L, 1L)).thenReturn(teamMember);
        Mockito.when(vacancyMapper.toEntity(vacancyDTO)).thenReturn(vacancy);
        Mockito.when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        Mockito.when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDTO);

        VacancyDTO result = vacancyService.create(vacancyDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(VacancyStatus.OPEN, result.getStatus());
        Mockito.verify(vacancyRepository).save(vacancy);
    }

    @Test
    void testCreateVacancy_TeamMemberWithoutPermissions_ShouldThrowException() {
        VacancyDTO vacancyDTO = new VacancyDTO();
        vacancyDTO.setProjectId(1L);

        Project project = new Project();
        project.setId(1L);

        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.ANALYST));

        Mockito.when(projectRepositoryAdapter.getById(1L)).thenReturn(project);
        Mockito.when(userContext.getUserId()).thenReturn(1L);
        Mockito.when(teamMemberRepositoryAdapter.getByUserIdAndProjectId(1L, 1L)).thenReturn(teamMember);

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class, () -> vacancyService.create(vacancyDTO));
        Assertions.assertEquals("team member must have roles OWNER or MANAGER", exception.getMessage());
    }

    @Test
    void testDeleteVacancy_Success() {
        Vacancy vacancy = new Vacancy();
        vacancy.setCandidates(new ArrayList<>());

        Mockito.when(vacancyRepositoryAdapter.getById(1L)).thenReturn(vacancy);
        vacancyService.deleteById(1L);

        Mockito.verify(candidateRepositoryAdapter).deleteAllCandidatesByVacancy(vacancy.getCandidates());
        Mockito.verify(vacancyRepository).delete(vacancy);
    }

    @Test
    void testUpdateVacancy_Success() {
        VacancyDTO vacancyDTO = new VacancyDTO();
        vacancyDTO.setStatus(VacancyStatus.CLOSED);
        vacancyDTO.setCandidateIds(List.of(1L));

        Project project = new Project();
        project.setId(1L);

        Vacancy vacancy = new Vacancy();
        vacancy.setCandidates(new ArrayList<>());
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setCount(1);
        vacancy.setPosition(TeamRole.DESIGNER);
        vacancy.setProject(project);

        Candidate candidate1 = new Candidate();
        candidate1.setId(1L);
        candidate1.setUserId(2L);
        candidate1.setCandidateStatus(CandidateStatus.ACCEPTED);

        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.MANAGER));
        teamMember.setUserId(1L);
        TeamMember teamMember2 = new TeamMember();
        teamMember2.setUserId(2L);
        teamMember2.setRoles(List.of(TeamRole.DESIGNER));
        Team team = new Team();
        team.setTeamMembers(List.of(teamMember, teamMember2));
        teamMember.setTeam(team);

        Mockito.when(vacancyRepositoryAdapter.getById(1L)).thenReturn(vacancy);
        Mockito.when(userContext.getUserId()).thenReturn(1L);
        Mockito.when(teamMemberRepositoryAdapter.getByUserIdAndProjectId(1L, vacancy.getProject().getId())).thenReturn(teamMember);
        Mockito.when(candidateRepositoryAdapter.getByIds(List.of(1L))).thenReturn(List.of(candidate1));
        Mockito.when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDTO);

        VacancyDTO result = vacancyService.update(1L, vacancyDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(VacancyStatus.CLOSED, vacancy.getStatus());
        Mockito.verify(vacancyRepository).save(vacancy);
    }

    @Test
    void testGetVacancyById_Success() {
        Vacancy vacancy = new Vacancy();
        VacancyDTO vacancyDTO = new VacancyDTO();

        Mockito.when(vacancyRepositoryAdapter.getById(1L)).thenReturn(vacancy);
        Mockito.when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDTO);

        VacancyDTO result = vacancyService.getById(1L);

        Assertions.assertNotNull(result);
        Mockito.verify(vacancyRepositoryAdapter).getById(1L);
        Mockito.verify(vacancyMapper).toDto(vacancy);
    }

    @Test
    void testGetAll_WithBothFilters() {
        TeamRole position = TeamRole.DEVELOPER;
        String name = "Java Developer";

        Vacancy vacancy = new Vacancy();
        VacancyDTO vacancyDTO = new VacancyDTO();

        Mockito.when(vacancyRepository.findAll(Mockito.any(Specification.class)))
                .thenReturn(List.of(vacancy));
        Mockito.when(vacancyMapper.toDtoList(List.of(vacancy)))
                .thenReturn(List.of(vacancyDTO));

        List<VacancyDTO> result = vacancyService.getAll(position, name);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Mockito.verify(vacancyRepository).findAll(Mockito.any(Specification.class));
        Mockito.verify(vacancyMapper).toDtoList(List.of(vacancy));
    }

    @Test
    void testGetAll_WithNoFilters() {
        Vacancy vacancy = new Vacancy();
        VacancyDTO vacancyDTO = new VacancyDTO();

        Mockito.when(vacancyRepository.findAll((Specification<Vacancy>) Mockito.isNull()))
                .thenReturn(List.of(vacancy));
        Mockito.when(vacancyMapper.toDtoList(List.of(vacancy)))
                .thenReturn(List.of(vacancyDTO));

        List<VacancyDTO> result = vacancyService.getAll(null, null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Mockito.verify(vacancyRepository).findAll((Specification<Vacancy>) Mockito.isNull());
        Mockito.verify(vacancyMapper).toDtoList(List.of(vacancy));
    }
}
