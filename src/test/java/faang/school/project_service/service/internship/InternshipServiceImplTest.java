package faang.school.project_service.service.internship;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.internship.InternshipRoleFilter;
import faang.school.projectservice.filter.internship.InternshipStatusFilter;
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
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.internship.InternshipServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceImplTest {

    private final InternshipMapper internshipMapper = Mappers.getMapper(InternshipMapper.class);
    private final int maxDuration = 3;
    private final LocalDateTime startDate = LocalDateTime.now();

    private CreateInternshipDto createInternshipDto = CreateInternshipDto.builder()
            .projectId(1L)
            .mentorId(1L)
            .internsIds(new ArrayList<>(List.of(3L, 4L)))
            .startDate(startDate)
            .endDate(startDate.plusDays(5))
            .build();

    private final Project project = Project.builder().id(createInternshipDto.projectId()).build();
    private final Team team = Team.builder().project(project).build();
    private final InternshipFilterDto internshipFilterDto
            = new InternshipFilterDto(TeamRole.DEVELOPER, InternshipStatus.CREATED);

    private final TeamMember internOne = TeamMember.builder()
            .id(createInternshipDto.internsIds().get(0))
            .team(team)
            .build();
    private final TeamMember internTwo = TeamMember.builder()
            .id(createInternshipDto.internsIds().get(1))
            .team(team)
            .build();

    private final Stage stage = Stage.builder()
            .executors(new ArrayList<>(List.of(internOne, internTwo)))
            .tasks(new ArrayList<>(List.of(Task.builder().status(TaskStatus.DONE).build())))
            .build();

    private final Internship internshipOne = new Internship();
    private final Internship internshipTwo = new Internship();


    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private TeamRepository teamRepository;

    private InternshipServiceImpl internshipService;

    @BeforeEach
    void setup() {
        internshipService = new InternshipServiceImpl(internshipRepository, projectRepository, internshipMapper,
                teamMemberRepository, userContext, teamRepository,
                List.of(new InternshipRoleFilter(), new InternshipStatusFilter()));

        ReflectionTestUtils.setField(internshipService, "maxInternshipDuration", maxDuration);

        project.setStages(new ArrayList<>(List.of(stage)));
        team.setTeamMembers(new ArrayList<>(List.of(internOne, internTwo)));
        internshipOne.setId(1L);
        internshipTwo.setId(2L);
    }

    @Test
    void testCreateGetProjectThrowsException() {
        when(projectRepository.getByIdOrThrow(createInternshipDto.projectId()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> internshipService.create(createInternshipDto));
    }

    @Test
    void testCreateGetTeamMemberThrowsException() {
        when(projectRepository.getByIdOrThrow(createInternshipDto.projectId())).thenReturn(Project.builder().build());
        when(teamMemberRepository.getByIdOrThrow(createInternshipDto.mentorId()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> internshipService.create(createInternshipDto));
    }

    @Test
    void testCreateThrowsExceptionIfNotAllInternsFoundInBase() {
        when(projectRepository.getByIdOrThrow(createInternshipDto.projectId())).thenReturn(Project.builder().build());
        when(teamMemberRepository.getByIdOrThrow(createInternshipDto.mentorId()))
                .thenReturn(TeamMember.builder().build());
        when(teamMemberRepository.findAllById(createInternshipDto.internsIds()))
                .thenReturn(List.of(TeamMember.builder().id(createInternshipDto.internsIds().get(0)).build()));

        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> internshipService.create(createInternshipDto));
        Assertions.assertEquals("Some interns not found. Found: %s Not found: %s"
                        .formatted(List.of(createInternshipDto.internsIds().get(0)),
                                List.of(createInternshipDto.internsIds().get(1))),
                entityNotFoundException.getLocalizedMessage());
    }

    @Test
    void testCreateThrowsExceptionIfMentorNotFromCurrentProject() {
        project.setId(project.getId() + 1);
        team.setProject(project);

        getCreateCustomMocks();
        when(teamMemberRepository.findAllById(createInternshipDto.internsIds())).thenReturn(List.of(
                TeamMember.builder().id(createInternshipDto.internsIds().get(0)).build(),
                TeamMember.builder().id(createInternshipDto.internsIds().get(1)).build()));

        DataValidationException dataValidationException = Assertions.assertThrows(DataValidationException.class,
                () -> internshipService.create(createInternshipDto));
        Assertions.assertEquals("Mentor %d is not from current project %d"
                        .formatted(createInternshipDto.mentorId(), createInternshipDto.projectId()),
                dataValidationException.getLocalizedMessage());
    }

    @Test
    void testCreateThrowsExceptionIfDurationOfInternshipIsMoreThanMax() {
        createInternshipDto = createInternshipDto.withEndDate(startDate.plusMonths(maxDuration).plusDays(1));

        getCreateCustomMocks();
        when(teamMemberRepository.findAllById(createInternshipDto.internsIds())).thenReturn(List.of(
                TeamMember.builder().id(createInternshipDto.internsIds().get(0)).build(),
                TeamMember.builder().id(createInternshipDto.internsIds().get(1)).build()));

        DataValidationException dataValidationException = Assertions.assertThrows(DataValidationException.class,
                () -> internshipService.create(createInternshipDto));
        Assertions.assertEquals("Duration of internship is more than %d months. Start date %s, end date %s"
                        .formatted(maxDuration, createInternshipDto.startDate(), createInternshipDto.endDate()),
                dataValidationException.getLocalizedMessage());
    }

    @Test
    void testCreateThrowsExceptionIfInternsNotFromCurrentProject() {
        Project wrongProject = Project.builder().id(34345L).build();

        Team wrongTeam = Team.builder().project(wrongProject).build();
        internTwo.setTeam(wrongTeam);

        getCreateCustomMocks();
        when(teamMemberRepository.findAllById(createInternshipDto.internsIds()))
                .thenReturn(List.of(internOne, internTwo));

        DataValidationException dataValidationException = Assertions.assertThrows(DataValidationException.class,
                () -> internshipService.create(createInternshipDto));
        Assertions.assertEquals("Some interns are not from current project. Interns: "
                        + createInternshipDto.internsIds(),
                dataValidationException.getLocalizedMessage());
    }

    @Test
    void testCreatePositive() {
        Internship internship = internshipMapper.toInternship(createInternshipDto);
        internship.setMentorId(TeamMember.builder().id(createInternshipDto.mentorId()).build());
        internship.setProject(Project.builder().id(createInternshipDto.projectId()).build());
        internship.setInterns(List.of(internOne, internTwo));

        getCreateCustomMocks();
        when(teamMemberRepository.findAllById(createInternshipDto.internsIds()))
                .thenReturn(List.of(internOne, internTwo));
        when(internshipRepository.save(any(Internship.class)))
                .thenReturn(internship);

        InternshipDto internshipDto = internshipService.create(createInternshipDto);

        Assertions.assertNotNull(internshipDto);
        assertThat(internshipDto).usingRecursiveComparison()
                .ignoringFields(InternshipDto.Fields.id, InternshipDto.Fields.status)
                .isEqualTo(createInternshipDto);

        verify(projectRepository).getByIdOrThrow(anyLong());
        verify(teamMemberRepository).getByIdOrThrow(anyLong());
        verify(teamMemberRepository).findAllById(anyIterable());
        verify(internshipRepository).save(any(Internship.class));
    }

    @Test
    void testUpdateGetInternshipThrowsExceptionIfNotFound() {
        long id = 1L;

        when(internshipRepository.getByIdOrThrow(id))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> internshipService
                .update(id, UpdateInternshipDto.builder().build()));
    }

    @Test
    void testUpdateThrowsExceptionIfTryingToAddInternsAfterStart() {
        UpdateInternshipDto updateInternshipDto = UpdateInternshipDto
                .builder()
                .internsIds(List.of(
                        internOne.getId() + internTwo.getId() + 1,
                        internOne.getId() + internTwo.getId() + 2))
                .build();

        Internship internship = new Internship();
        internship.setId(1L);
        internship.setInterns(List.of(internOne, internTwo));
        internship.setStartDate(LocalDateTime.now().minusDays(1L));

        getUpdateCustomMocks(internship);

        DataValidationException dataValidationException = Assertions.assertThrows(DataValidationException.class,
                () -> internshipService.update(internship.getId(), updateInternshipDto));
        Assertions.assertEquals("Cant add new interns after internship started",
                dataValidationException.getMessage());
    }

    @Test
    void testUpdateThrowsExceptionIfTryingToChangeOnMentorNotFromCurrentProject() {
        Project wrongProject = Project.builder().id(34345L).build();
        Team wrongTeam = Team.builder().project(wrongProject).build();
        TeamMember mentor = TeamMember.builder()
                .id(22L)
                .team(wrongTeam)
                .build();

        UpdateInternshipDto updateInternshipDto = UpdateInternshipDto
                .builder()
                .mentorId(mentor.getId())
                .internsIds(List.of(
                        internOne.getId() + internTwo.getId() + 1,
                        internOne.getId() + internTwo.getId() + 2))
                .build();

        Internship internship = new Internship();
        internship.setProject(project);
        internship.setMentorId(TeamMember.builder().id(updateInternshipDto.mentorId() + 1).build());
        internship.setId(1L);
        internship.setInterns(List.of(internOne, internTwo));
        internship.setStartDate(LocalDateTime.now().plusDays(1L));

        when(teamMemberRepository.getByIdOrThrow(updateInternshipDto.mentorId())).thenReturn(mentor);
        getUpdateCustomMocks(internship);

        DataValidationException dataValidationException = Assertions.assertThrows(DataValidationException.class,
                () -> internshipService.update(internship.getId(), updateInternshipDto));
        Assertions.assertEquals("Mentor %d not from current project %d"
                        .formatted(mentor.getId(), internship.getProject().getId()),
                dataValidationException.getMessage());
    }

    @Test
    void testUpdateThrowsExceptionIfInternshipDurationIsMoreThanMax() {
        UpdateInternshipDto updateInternshipDto = UpdateInternshipDto
                .builder()
                .internsIds(List.of(
                        internOne.getId() + internTwo.getId() + 1,
                        internOne.getId() + internTwo.getId() + 2))
                .endDate(LocalDateTime.now().plusMonths(maxDuration).plusDays(2))
                .build();

        Internship internship = new Internship();
        internship.setProject(project);
        internship.setId(1L);
        internship.setInterns(List.of(internOne, internTwo));
        internship.setStartDate(LocalDateTime.now().plusDays(1));

        getUpdateCustomMocks(internship);

        DataValidationException dataValidationException = Assertions.assertThrows(DataValidationException.class,
                () -> internshipService.update(internship.getId(), updateInternshipDto));
        Assertions.assertEquals("Duration of internship is more than %d months. Start date %s, end date %s"
                        .formatted(maxDuration, internship.getStartDate(), updateInternshipDto.endDate()),
                dataValidationException.getMessage());
    }

    @Test
    void testUpdatePositiveWhenAllInternsCompleteInternship() {
        UpdateInternshipDto updateInternshipDto = UpdateInternshipDto
                .builder()
                .endDate(LocalDateTime.now().plusMonths(maxDuration).minusMonths(1))
                .build();

        Internship internship = new Internship();
        internship.setProject(project);
        internship.setId(1L);
        internship.setInterns(List.of(internOne, internTwo));
        internship.setStartDate(LocalDateTime.now().minusDays(1));
        internship.setStatus(InternshipStatus.COMPLETED);
        internship.setRole(TeamRole.DEVELOPER);

        getUpdateCustomMocks(internship);
        when(internshipRepository.save(any(Internship.class))).thenReturn(internship);

        InternshipDto updatedInternship = internshipService.update(internship.getId(), updateInternshipDto);

        ArgumentCaptor<TeamMember> captor = ArgumentCaptor.forClass(TeamMember.class);
        verify(teamMemberRepository, times(2)).save(captor.capture());

        List<TeamMember> completedInternshipInterns = captor.getAllValues();

        Assertions.assertEquals(2, completedInternshipInterns.size());
        Assertions.assertTrue(completedInternshipInterns.stream().map(TeamMember::getId)
                .toList().containsAll(internship.getInterns().stream().map(TeamMember::getId).toList()));
        completedInternshipInterns.stream().forEach(intern -> Assertions
                .assertTrue(intern.getRoles().contains(internship.getRole())));
        Assertions.assertEquals(internship.getId(), updatedInternship.id());
        Assertions.assertEquals(internship.getStatus(), updatedInternship.status());
    }

    @Test
    void testUpdatePositiveWhenOneInternCompleteAndOneFailedInternship() {
        Stage stageWithDoneTask = Stage.builder()
                .executors(new ArrayList<>(List.of(internOne)))
                .tasks(new ArrayList<>(List.of(Task.builder().status(TaskStatus.DONE).build())))
                .build();

        Stage stageWithReviewTask = Stage.builder()
                .executors(new ArrayList<>(List.of(internTwo)))
                .tasks(new ArrayList<>(List.of(Task.builder().status(TaskStatus.REVIEW).build())))
                .build();

        project.setStages(new ArrayList<>(List.of(stageWithReviewTask, stageWithDoneTask)));

        UpdateInternshipDto updateInternshipDto = UpdateInternshipDto
                .builder()
                .endDate(LocalDateTime.now().plusMonths(maxDuration).minusMonths(1))
                .build();

        Internship internship = new Internship();
        internship.setProject(project);
        internship.setId(1L);
        internship.setInterns(new ArrayList<>(List.of(internOne, internTwo)));
        internship.setStartDate(LocalDateTime.now().minusDays(1));
        internship.setStatus(InternshipStatus.COMPLETED);
        internship.setRole(TeamRole.DEVELOPER);

        getUpdateCustomMocks(internship);
        when(internshipRepository.save(any(Internship.class))).thenReturn(internship);

        InternshipDto updatedInternship = internshipService.update(internship.getId(), updateInternshipDto);

        ArgumentCaptor<TeamMember> teamMemberCaptor = ArgumentCaptor.forClass(TeamMember.class);
        ArgumentCaptor<Team> teamCaptor = ArgumentCaptor.forClass(Team.class);

        verify(teamMemberRepository, times(1)).save(teamMemberCaptor.capture());
        verify(teamRepository, times(1)).save(teamCaptor.capture());

        TeamMember completedInternshipIntern = teamMemberCaptor.getValue();
        Team teamAfterUpdate = teamCaptor.getValue();

        Assertions.assertEquals(internOne.getId(), completedInternshipIntern.getId());
        Assertions.assertTrue(completedInternshipIntern.getRoles().contains(internship.getRole()));
        Assertions.assertEquals(internship.getId(), updatedInternship.id());
        Assertions.assertEquals(internship.getStatus(), updatedInternship.status());
        Assertions.assertEquals(1, teamAfterUpdate.getTeamMembers().size());
        Assertions.assertTrue(teamAfterUpdate.getTeamMembers().contains(internOne));
        Assertions.assertFalse(teamAfterUpdate.getTeamMembers().contains(internTwo));
    }

    @Test
    void testGetByFiltersReturnAllResultsIfFilterDtoIsNull() {
        getFiltersCustomMocks();

        List<InternshipDto> internshipsByFilters = internshipService.getByFilters(null);
        Assertions.assertEquals(Stream.of(internshipOne, internshipTwo).map(Internship::getId).sorted().toList(),
                internshipsByFilters.stream().map(InternshipDto::id).sorted().toList());
    }

    @Test
    void testGetByFiltersPositive() {
        internshipOne.setRole(internshipFilterDto.role());
        internshipOne.setStatus(internshipFilterDto.status());

        internshipTwo.setRole(TeamRole.ANALYST);
        internshipTwo.setStatus(internshipFilterDto.status());

        getFiltersCustomMocks();

        List<InternshipDto> internshipsByFilters = internshipService.getByFilters(internshipFilterDto);
        Assertions.assertEquals(1, internshipsByFilters.size());
        Assertions.assertEquals(internshipOne.getId(), internshipsByFilters.get(0).id());
    }

    @Test
    void testGetAllPositive() {
        when(internshipRepository.findAll()).thenReturn(List.of(internshipOne, internshipTwo));

        List<InternshipDto> internshipsByFilters = internshipService.getAll();
        Assertions.assertEquals(Stream.of(internshipOne, internshipTwo).map(Internship::getId).sorted().toList(),
                internshipsByFilters.stream().map(InternshipDto::id).sorted().toList());
    }

    @Test
    void testGetByIdThrowsExceptionIfInternshipNotFound() {
        long id = 1L;
        when(internshipRepository.getByIdOrThrow(id)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> internshipService.getById(id));
    }

    @Test
    void testGetByIdPositive() {
        internshipOne.setName("new internship");

        when(internshipRepository.getByIdOrThrow(internshipOne.getId())).thenReturn(internshipOne);

        InternshipDto internshipById = internshipService.getById(internshipOne.getId());

        Assertions.assertEquals(internshipOne.getId(), internshipById.id());
        Assertions.assertEquals(internshipOne.getName(), internshipById.name());
    }

    private void getCreateCustomMocks() {
        when(projectRepository.getByIdOrThrow(createInternshipDto.projectId())).thenReturn(Project.builder().build());
        when(teamMemberRepository.getByIdOrThrow(createInternshipDto.mentorId()))
                .thenReturn(TeamMember
                        .builder()
                        .team(team)
                        .build());
    }

    private void getUpdateCustomMocks(Internship internship) {
        when(internshipRepository.getByIdOrThrow(internship.getId())).thenReturn(internship);
    }

    private void getFiltersCustomMocks() {
        when(internshipRepository.findAll()).thenReturn(List.of(internshipOne, internshipTwo));
    }
}