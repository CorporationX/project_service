package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.TeamRoleDto;
import faang.school.projectservice.exception.internship.EntityNotFoundException;
import faang.school.projectservice.exception.internship.IncorrectInternshipDateTimeException;
import faang.school.projectservice.exception.internship.InternshipUpdateException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.InternshipServiceImpl;
import faang.school.projectservice.service.filter.InternshipFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceImplTest {
    private static final LocalDateTime START_DATE = LocalDateTime.of(2024, Month.AUGUST, 1, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2024, Month.OCTOBER, 30, 0, 0);
    private static final LocalDateTime WRONG_DATE = LocalDateTime.of(2024, Month.DECEMBER, 30, 0, 0);
    @InjectMocks
    private InternshipServiceImpl internshipService;

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private ProjectRepository projectRepository;

    private List<InternshipFilter> filters;

    private InternshipMapper internshipMapper;

    private CreateInternshipDto internshipDto;

    @BeforeEach
    public void setUp() {
        InternshipFilter filterMock1 = Mockito.mock(InternshipFilter.class);
        InternshipFilter filterMock2 = Mockito.mock(InternshipFilter.class);
        internshipMapper = Mappers.getMapper(InternshipMapper.class);
        filters = List.of(filterMock1, filterMock2);
        internshipService = new InternshipServiceImpl(internshipMapper, filters, internshipRepository, projectRepository);
        internshipDto = createCreateInternshipDto();
    }

    @Test
    public void testCreateInternship_Success() {
        Project project = createProjectWithTeam();

        when(projectRepository.getProjectById(internshipDto.getProjectId())).thenReturn(project);

        internshipService.createInternship(internshipDto);

        verify(internshipRepository, times(1)).save(any(Internship.class));
    }

    @Test
    public void testCreateInternship_ProjectNotFound() {
        when(projectRepository.getProjectById(internshipDto.getProjectId()))
                .thenThrow(new EntityNotFoundException("Project not found"));

        assertThrows(EntityNotFoundException.class, () -> internshipService.createInternship(internshipDto));
    }

    @Test
    public void testCreateInternship_WrongDuration() {
        Project project = createProjectWithTeam();
        CreateInternshipDto internshipDto = createInternshipDtoWithWrongDuration();

        when(projectRepository.getProjectById(internshipDto.getProjectId())).thenReturn(project);

        assertThrows(IncorrectInternshipDateTimeException.class, () -> internshipService.createInternship(internshipDto));
    }

    @Test
    public void testCreateInternship_WrongMentor() {
        Project project = createProjectWithWrongMentor();

        when(projectRepository.getProjectById(internshipDto.getProjectId())).thenReturn(project);

        assertThrows(EntityNotFoundException.class, () -> internshipService.createInternship(internshipDto));
    }

    @Test
    public void testUpdateInternship_InternshipNotFound() {
        Long internshipId = 1L;
        TeamRoleDto teamRole = new TeamRoleDto("DEVELOPER");

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> internshipService.updateInternship(internshipId, teamRole));
    }

    @Test
    public void testUpdateInternship_InternshipNotCompleted() {
        Long internshipId = 1L;
        Internship internship = internshipMapper.createInternshipDtoToInternship(internshipDto);
        internship.setStatus(InternshipStatus.IN_PROGRESS);
        TeamRoleDto teamRole = new TeamRoleDto("DEVELOPER");
        when(internshipRepository.findById(internshipId)).thenReturn(Optional.of(internship));

        assertThrows(InternshipUpdateException.class, () -> internshipService.updateInternship(internshipId, teamRole));
    }

    @Test
    public void testUpdateInternship_AllTasksCompleted() {
        TeamRoleDto teamRole = new TeamRoleDto("DEVELOPER");
        Internship internship = createInternshipWithCompletedTasks();

        when(internshipRepository.findById(internship.getId())).thenReturn(Optional.of(internship));

        internshipService.updateInternship(internship.getId(), teamRole);

        assertEquals(TeamRole.DEVELOPER, internship.getInterns().get(0).getRoles().get(0));
        verify(internshipRepository, times(1)).findById(internship.getId());
        verify(internshipRepository, times(1)).save(internship);
    }

    @Test
    public void testUpdateInternship_TasksNotCompleted() {
        TeamRoleDto teamRole = new TeamRoleDto("DEVELOPER");
        Internship internship = createInternshipWithNotCompletedTasks();

        when(internshipRepository.findById(internship.getId())).thenReturn(Optional.of(internship));

        internshipService.updateInternship(internship.getId(), teamRole);

        assertTrue(internship.getInterns().isEmpty());
        verify(internshipRepository, times(1)).findById(internship.getId());
        verify(internshipRepository, times(1)).save(internship);
    }

    @Test
    public void testUpdateInternship_NotInterns() {
        Project project = createProjectWithoutInterns();

        when(projectRepository.getProjectById(internshipDto.getProjectId())).thenReturn(project);

        assertThrows(EntityNotFoundException.class, () -> internshipService.createInternship(internshipDto));
    }

    @Test
    public void testGetAllInternships_WithFilters() {
        InternshipFilterDto filterDto =
                new InternshipFilterDto(InternshipStatus.COMPLETED, new TeamRoleDto("DEVELOPER"));
        List<Internship> internships = createInternships();
        List<InternshipDto> internshipDtos = internshipMapper.internshipsToInternshipDtos(internships);

        when(filters.get(0).isApplicable(filterDto)).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(internships.stream());
        when(filters.get(1).isApplicable(filterDto)).thenReturn(true);
        when(filters.get(1).apply(any(), any())).thenReturn(internships.stream());
        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> result = internshipService.getAllInternships(filterDto);

        assertEquals(internshipDtos, result);
        verify(internshipRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllInternships_WithoutFilters() {
        List<Internship> internships = createInternships();
        List<InternshipDto> internshipDtos = internshipMapper.internshipsToInternshipDtos(internships);

        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> result = internshipService.getAllInternships(null);

        assertEquals(internshipDtos, result);
        verify(internshipRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllInternships_NoInternships() {
        TeamRoleDto teamRole = new TeamRoleDto("DEVELOPER");
        InternshipFilterDto filterDto = new InternshipFilterDto(InternshipStatus.COMPLETED, teamRole);
        List<Internship> internships = new ArrayList<>();

        when(internshipRepository.findAll()).thenReturn(internships);

        assertThrows(EntityNotFoundException.class, () -> internshipService.getAllInternships(filterDto));
        verify(internshipRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllInternshipsByProjectId_Success() {
        Long projectId = 1L;
        List<Internship> internships = createInternships();
        List<InternshipDto> internshipDtos = internshipMapper.internshipsToInternshipDtos(internships);

        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> result = internshipService.getAllInternshipsByProjectId(projectId);

        assertEquals(internshipDtos, result);
        verify(internshipRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllInternshipsByProjectId_NoInternships() {
        List<Internship> internships = new ArrayList<>();

        when(internshipRepository.findAll()).thenReturn(internships);

        assertThrows(EntityNotFoundException.class, () -> internshipService.getAllInternshipsByProjectId(1L));
        verify(internshipRepository, times(1)).findAll();
    }

    @Test
    public void testGetInternshipById_Success() {
        Internship internship = new Internship();
        internship.setId(1L);
        InternshipDto internshipDto = internshipMapper.internshipToInternshipDto(internship);

        when(internshipRepository.findById(internship.getId())).thenReturn(Optional.of(internship));

        InternshipDto result = internshipService.getInternshipById(internship.getId());

        assertEquals(internshipDto, result);
        verify(internshipRepository, times(1)).findById(internship.getId());
    }

    @Test
    public void testGetInternshipById_NotInternship() {
        Long internshipId = 1L;

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> internshipService.getInternshipById(internshipId));
        verify(internshipRepository, times(1)).findById(internshipId);
    }

    private CreateInternshipDto createCreateInternshipDto() {
        List<Long> interns = List.of(1L);
        Long mentorId = 2L;
        Long projectId = 1L;

        return new CreateInternshipDto("name", "description", projectId, interns, mentorId, START_DATE, END_DATE);
    }

    private CreateInternshipDto createInternshipDtoWithWrongDuration() {
        List<Long> interns = List.of(1L);
        Long mentorId = 2L;
        Long projectId = 1L;

        return new CreateInternshipDto("name", "description", projectId, interns, mentorId, START_DATE, WRONG_DATE);
    }

    private Project createProjectWithTeam() {
        Project project = new Project();
        project.setId(1L);
        Team team = new Team();
        TeamMember intern = new TeamMember();
        intern.setId(1L);
        TeamMember mentor = new TeamMember();
        mentor.setId(2L);
        intern.setRoles(List.of(TeamRole.INTERN));
        mentor.setRoles(List.of(TeamRole.DEVELOPER));
        team.setTeamMembers(List.of(intern, mentor));
        project.setTeams(List.of(team));
        return project;
    }

    private Project createProjectWithWrongMentor() {
        Project project = new Project();
        project.setId(1L);
        Team team = new Team();
        TeamMember intern = new TeamMember();
        intern.setId(1L);
        TeamMember mentor = new TeamMember();
        mentor.setId(3L);
        intern.setRoles(List.of(TeamRole.INTERN));
        mentor.setRoles(List.of(TeamRole.DEVELOPER));
        team.setTeamMembers(List.of(intern, mentor));
        project.setTeams(List.of(team));
        return project;
    }

    private Project createProjectWithoutInterns() {
        Project project = new Project();
        project.setId(1L);
        Team team = new Team();
        TeamMember mentor = new TeamMember();
        mentor.setId(3L);
        mentor.setRoles(List.of(TeamRole.DEVELOPER));
        team.setTeamMembers(List.of(mentor));
        project.setTeams(List.of(team));
        return project;
    }

    private Internship createInternshipWithCompletedTasks() {
        Internship internship = new Internship();
        internship.setId(1L);
        internship.setStatus(InternshipStatus.COMPLETED);
        TeamMember intern = new TeamMember();
        intern.setId(1L);
        intern.setRoles(new ArrayList<>(List.of(TeamRole.INTERN)));
        intern.setStages(createStagesWithDoneTasks());
        internship.setInterns(new ArrayList<>(List.of(intern)));
        return internship;
    }

    private Internship createInternshipWithNotCompletedTasks() {
        Internship internship = new Internship();
        internship.setId(1L);
        internship.setStatus(InternshipStatus.COMPLETED);
        TeamMember intern = new TeamMember();
        intern.setId(1L);
        intern.setRoles(new ArrayList<>(List.of(TeamRole.INTERN)));
        intern.setStages(createStagesWithNotDoneTasks());
        internship.setInterns(new ArrayList<>(List.of(intern)));
        return internship;
    }

    private List<Internship> createInternships() {
        TeamMember mentor1 = new TeamMember();
        TeamMember mentor2 = new TeamMember();
        Project project1 = new Project();
        project1.setId(1L);
        mentor1.setId(1L);
        mentor2.setId(2L);
        Internship internship1 = new Internship();
        internship1.setId(1L);
        internship1.setName("name1");
        internship1.setDescription("description1");
        internship1.setMentorId(mentor1);
        internship1.setProject(project1);
        internship1.setStatus(InternshipStatus.COMPLETED);
        internship1.setEndDate(END_DATE);
        Internship internship2 = new Internship();
        internship2.setId(2L);
        internship2.setName("name2");
        internship2.setDescription("description2");
        internship2.setMentorId(mentor2);
        internship2.setProject(project1);
        internship2.setStatus(InternshipStatus.COMPLETED);
        internship2.setEndDate(END_DATE);
        return List.of(internship1, internship2);
    }

    private List<Stage> createStagesWithDoneTasks() {
        List<Stage> stages = new ArrayList<>();
        Stage stage = new Stage();
        List<Task> tasks = new ArrayList<>();
        Task task = new Task();
        task.setStatus(TaskStatus.DONE);
        tasks.add(task);
        stage.setTasks(tasks);
        stages.add(stage);
        return stages;
    }

    private List<Stage> createStagesWithNotDoneTasks() {
        List<Stage> stages = new ArrayList<>();
        Stage stage = new Stage();
        List<Task> tasks = new ArrayList<>();
        Task task = new Task();
        task.setStatus(TaskStatus.IN_PROGRESS);
        tasks.add(task);
        stage.setTasks(tasks);
        stages.add(stage);
        return stages;
    }
}
