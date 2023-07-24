package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.ResponseInternshipDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.filter.internship.StatusFilter;
import faang.school.projectservice.filter.internship.TeamRoleFilter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {
    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private ProjectJpaRepository projectRepository;

    @Mock
    private TeamMemberJpaRepository teamMemberRepository;

    @Mock
    private TaskRepository taskRepository;

    @Spy
    private InternshipMapper internshipMapper = InternshipMapper.INSTANCE;

    @InjectMocks
    private InternshipService internshipService;

    @Test
    public void testCreate() {
        CreateInternshipDto dto = new CreateInternshipDto(
                1L,
                2L,
                List.of(3L, 4L),
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(2),
                "Internship",
                TeamRole.INTERN_DEVELOPER,
                5L
        );


        Project project = Project.builder().id(1L).build();
        TeamMember mentor = TeamMember.builder().id(2L).build();
        List<TeamMember> interns = new ArrayList<>(List.of(
                TeamMember.builder().id(3L).build(),
                TeamMember.builder().id(4L).build()
        ));
        Internship savedInternship = new Internship();
        savedInternship.setProject(project);
        savedInternship.setMentor(mentor);
        savedInternship.setInterns(interns);

        when(projectRepository.findById(dto.getProjectId())).thenReturn(Optional.of(project));
        when(teamMemberRepository.findByIdAndProjectId(dto.getMentorId(), dto.getProjectId())).thenReturn(mentor);
        when(teamMemberRepository.findAllById(dto.getInternIds())).thenReturn(interns);
        when(internshipRepository.save(any(Internship.class))).thenReturn(savedInternship);

        ResponseInternshipDto result = internshipService.create(dto);

        assertEquals(savedInternship.getProject().getId(), result.getProjectId());
        assertEquals(savedInternship.getMentor().getId(), result.getMentorId());
        assertEquals(savedInternship.getInterns().size(), result.getInternIds().size());
        assertEquals(savedInternship.getStartDate(), result.getStartDate());
        assertEquals(savedInternship.getEndDate(), result.getEndDate());
        assertEquals(savedInternship.getName(), result.getName());
        assertEquals(savedInternship.getCreatedBy(), result.getCreatedBy());
    }

    @Test
    public void testUpdate() {
        UpdateInternshipDto dto = UpdateInternshipDto.builder()
                .id(1L)
                .status(InternshipStatus.COMPLETED)
                .name("Updated Internship")
                .updatedBy(2L)
                .build();

        List<Task> tasks = Arrays.asList(Task.builder().status(TaskStatus.DONE).build(), Task.builder().status(TaskStatus.IN_PROGRESS).build());
        List<Task> tasks2 = Arrays.asList(Task.builder().status(TaskStatus.DONE).build(), Task.builder().status(TaskStatus.DONE).build());

        List<TeamMember> interns = new ArrayList<>(List.of(
                TeamMember.builder().id(3L).build(),
                TeamMember.builder().id(4L).build()
        ));
        List<Team> team = new ArrayList<>(List.of(Team.builder().teamMembers(interns).build()));
        Project project = Project.builder().id(1L).teams(team).build();
        Internship internship = new Internship();
        internship.setProject(project);
        internship.setStatus(InternshipStatus.IN_PROGRESS);
        internship.setInterns(interns);

        when(internshipRepository.findById(dto.getId())).thenReturn(Optional.of(internship));
        when(taskRepository.findAllByProjectIdAndPerformerUserId(anyLong(), anyLong())).thenReturn(tasks).thenReturn(tasks2);

        ResponseInternshipDto result = internshipService.update(dto);

        assertEquals(dto.getStatus(), result.getStatus());
    }

    @Test
    public void testFindByFilter() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder()
                .projectId(1L)
                .status(InternshipStatus.COMPLETED)
                .internshipRole(TeamRole.INTERN_DEVELOPER)
                .build();

        List<InternshipFilter> filter = List.of(
                new TeamRoleFilter(),
                new StatusFilter()
        );
        internshipService = new InternshipService(internshipRepository, projectRepository,
                teamMemberRepository, taskRepository, internshipMapper, filter);
        Internship internship1 = Internship.builder()
                .status(InternshipStatus.IN_PROGRESS)
                .interns(List.of(TeamMember.builder().roles(List.of(TeamRole.INTERN_DEVELOPER)).build()))
                .build();
        Internship internship2 = Internship.builder()
                .status(InternshipStatus.COMPLETED)
                .interns(List.of(TeamMember.builder().roles(List.of(TeamRole.INTERN_ANALYST)).build()))
                .build();
        Internship internship3 = Internship.builder()
                .status(InternshipStatus.COMPLETED)
                .interns(List.of(TeamMember.builder().roles(List.of(TeamRole.INTERN_DEVELOPER)).build()))
                .build();
        List<Internship> internships = Arrays.asList(internship1, internship2, internship3);

        when(internshipRepository.findAllByProjectId(filterDto.getProjectId())).thenReturn(internships);

        List<ResponseInternshipDto> results = internshipService.findByFilter(filterDto);

        assertEquals(1, results.size());
    }

    @Test
    public void testFindAll() {
        List<TeamMember> interns = new ArrayList<>();
        Internship internship = Internship.builder().interns(interns).build();
        List<Internship> internships = Arrays.asList(internship, internship);

        when(internshipRepository.findAll()).thenReturn(internships);

        List<ResponseInternshipDto> results = internshipService.findAll();

        assertEquals(2, results.size());
    }

    @Test
    public void testFindById() {
        List<TeamMember> interns = new ArrayList<>();
        Internship internship = Internship.builder().interns(interns).build();

        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));

        ResponseInternshipDto result = internshipService.findById(1L);

        assertNotNull(result);
    }
}
