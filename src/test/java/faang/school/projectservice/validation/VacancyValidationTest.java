package faang.school.projectservice.validation;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exceptions.DataVacancyValidation;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VacancyValidationTest {
    private ProjectRepository projectRepository;
    private TeamMemberRepository teamMemberRepository;
    private VacancyMapper vacancyMapper;
    private VacancyRepository vacancyRepository;
    private VacancyValidation vacancyValidation;
    @BeforeEach
    void setup(){
        projectRepository = mock(ProjectRepository.class);
        teamMemberRepository = mock(TeamMemberRepository.class);
        vacancyMapper = mock(VacancyMapper.class);
        vacancyRepository = mock(VacancyRepository.class);
        vacancyValidation = new VacancyValidation(projectRepository,vacancyMapper,teamMemberRepository,vacancyRepository);
    }

    @Test
    public void testValidationProjectNameIsNullT() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setProjectId(1L);
        Project project = new Project();
        project.setId(1L);
        project.setName(null);
        when(projectRepository.getProjectById(vacancyDto.getProjectId())).thenReturn(project);
        Assert.assertThrows(DataVacancyValidation.class, () -> vacancyValidation.validationCreate(vacancyDto));
    }

    @Test
    public void testValidationProjectNameIsBlank() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setProjectId(1L);
        Project project = new Project();
        project.setId(1L);
        project.setName("  ");
        when(projectRepository.getProjectById(vacancyDto.getProjectId())).thenReturn(project);
        Assert.assertThrows("Не указано название проекта", DataVacancyValidation.class, () -> vacancyValidation.validationCreate(vacancyDto));
    }

    @Test
    public void testValidationTutorIsNull() {
        VacancyDto vacancyDto = new VacancyDto();
        Vacancy vacancy = new Vacancy();
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        Assert.assertThrows("Ответственный не назначен", DataVacancyValidation.class, () -> vacancyValidation.validationTutor(vacancyDto));
    }

    @Test
    public void testValidationTutorRole() {
        VacancyDto vacancyDto = new VacancyDto();

        Vacancy vacancy = new Vacancy();
        vacancy.setCreatedBy(1L);

        Long tutorId = 1L;
        TeamMember tutor = new TeamMember();
        tutor.setId(tutorId);
        tutor.setRoles(List.of(TeamRole.DESIGNER));

        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(teamMemberRepository.findById(tutorId)).thenReturn(tutor);
        Assert.assertThrows("Данный сотрудник не является куратором", DataVacancyValidation.class, () -> vacancyValidation.validationTutor(vacancyDto));
    }

    @Test
    public void testValidationVacancyIfCandidateNeed() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setId(1L);
        Candidate candidate1 = new Candidate();
        candidate1.setCandidateStatus(CandidateStatus.ACCEPTED);
        Candidate candidate2 = new Candidate();

        List<Candidate> candidates = List.of(candidate1, candidate2);
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setCount(2);
        vacancy.setCandidates(candidates);

        when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.of(vacancy));
        Assert.assertThrows("Вакансия не может быть закрыта, нужное количество не набрано", DataVacancyValidation.class, () -> vacancyValidation.validationVacancyIfCandidateNeed(vacancyDto));
    }

    @Test
    public void testValidationVacancyClosed() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setId(1L);

        Candidate candidate1 = new Candidate();
        candidate1.setId(1L);
        Candidate candidate2 = new Candidate();
        candidate2.setId(2L);
        List<Candidate> candidates = List.of(candidate1, candidate2);

        List<TeamRole> roles = List.of(TeamRole.DEVELOPER);
        TeamMember tM1 = new TeamMember();
        tM1.setId(1L);
        tM1.setRoles(roles);

        TeamMember tM2 = new TeamMember();
        tM1.setId(2L);
        List<TeamMember> teamMembers = List.of(tM1, tM2);

        Team team = new Team();
        team.setTeamMembers(teamMembers);
        List<Team> teams = List.of(team);

        Project project = new Project();
        project.setTeams(teams);

        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setProject(project);
        vacancy.setCandidates(candidates);

        when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.of(vacancy));
        Assert.assertThrows("Вакансия не может быть закрыта, пока вся команда не получит свои роли", DataVacancyValidation.class, () -> vacancyValidation.validationVacancyClosed(vacancyDto));
    }
}
