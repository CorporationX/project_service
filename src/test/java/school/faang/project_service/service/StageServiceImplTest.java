package school.faang.project_service.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.stage.AllStageFilterDto;
import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationCreateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.mapper.StageMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.StageInvitationServiceImpl;
import faang.school.projectservice.service.StageServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageServiceImplTest {
    @Spy
    private StageMapperImpl stageMapper;
    @Mock
    private StageRepository stageRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private StageRolesRepository stageRolesRepository;
    @Mock
    private UserContext userContext;

    @Mock
    private StageInvitationServiceImpl stageInvitationService;

    @InjectMocks
    private StageServiceImpl service;

    @Captor
    ArgumentCaptor<Stage> captorStage;

    @Captor
    ArgumentCaptor<List<StageRoles>> captorList;

    @Test
    public void createStage_userNotValid_shouldThrowEntityNotFoundException() {
        StageCreateDto dto = StageCreateDto.builder()
                .projectId(1L)
                .stageName("Test")
                .teamRoles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .executorsId(new ArrayList<>(List.of(1L, 2L, 3L, 4L, 5L)))
                .build();

        when(teamMemberRepository.existsById(0L)).thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.createStage(dto));
    }

    @Test
    public void createStage_existProject_shouldThrowEntityNotFoundException() {
        StageCreateDto dto = StageCreateDto.builder()
                .projectId(1L)
                .stageName("Test")
                .teamRoles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .executorsId(new ArrayList<>(List.of(1L, 2L, 3L, 4L, 5L)))
                .build();

        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.createStage(dto));
    }

    @Test
    public void createStage_notValidProject_shouldThrowForbiddenException() {
        StageCreateDto dto = StageCreateDto.builder()
                .projectId(1L)
                .stageName("Test")
                .teamRoles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .executorsId(new ArrayList<>(List.of(1L, 2L, 3L, 4L, 5L)))
                .build();
        Project project = new Project();
        project.setStatus(ProjectStatus.CANCELLED);
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Assertions.assertThrows(ForbiddenException.class,
                () -> service.createStage(dto));
    }

    @Test
    public void createStage_createStage_shouldCreateStageAndSaveStageRole() {
        StageCreateDto dto = StageCreateDto.builder()
                .projectId(1L)
                .stageName("Test Stage")
                .teamRoles(List.of(TeamRole.DEVELOPER, TeamRole.DEVELOPER, TeamRole.DESIGNER))
                .executorsId(List.of(1L))
                .build();
        Project project = new Project();
        Stage stage = new Stage();
        stage.setStageId(5L);
        List<TeamMember> teamMembers = List.of(new TeamMember(1L, 11L, "Test1", new ArrayList<>(List.of(TeamRole.DEVELOPER)), new Team(), new ArrayList<>()));
        project.setStatus(ProjectStatus.IN_PROGRESS);
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(teamMemberRepository.findAllByIdIn(dto.executorsId())).thenReturn(teamMembers);
        when(stageMapper.toEntityCreate(dto, teamMembers, project, new ArrayList<>())).thenReturn(stage);


        service.createStage(dto);


        verify(stageRolesRepository).saveAll(captorList.capture());
        List<StageRoles> stageRoles = captorList.getValue();
        verify(stageRepository).save(captorStage.capture());
        Stage verifyStage = captorStage.getValue();


        Assertions.assertEquals(5L, verifyStage.getStageId());
        Assertions.assertEquals(2L, stageRoles.size());
    }

    @Test
    public void getAllStageByFilter_responseStageDto_shouldResponseListDto() {
        List<TeamRole> teamRoleList = new ArrayList<>(List.of(TeamRole.DEVELOPER));
        AllStageFilterDto dto = AllStageFilterDto.builder()
                .projectId(1L)
                .taskStatus(TaskStatus.DONE)
                .teamRoleList(teamRoleList)
                .build();
        List<Stage> stageList = new ArrayList<>(List.of());
        StageDto stageDto = StageDto.builder()
                .stageId(1L)
                .build();
        List<StageDto> mapperDto = new ArrayList<>(List.of(stageDto));
        when(stageMapper.toListDto(stageList)).thenReturn(mapperDto);

        List<StageDto> responseDto = service.getAllStageByFilter(dto);

        Assertions.assertEquals(stageDto.stageId(), responseDto.get(0).stageId());
    }

    @Test
    public void deleteStage_notFoundProject_shouldThrowEntityNotFoundException() {
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.deleteStage(1L, 1L));
    }

    @Test
    public void deleteStage_notExistProject_shouldThrowForbiddenException() {
        Project project = new Project();
        project.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        Assertions.assertThrows(ForbiddenException.class,
                () -> service.deleteStage(1L, 1L));
    }

    @Test
    public void deleteStage_userNotValid_shouldEntityNotFoundException() {
        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.deleteStage(1L, 1L));
    }

    @Test
    public void deleteStage_stageNotExist_shouldEntityNotFoundException() {
        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);
        when(teamMemberRepository.existsById(0L)).thenReturn(true);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.deleteStage(1L, 1L));
    }

    @Test
    public void deleteStage_deleteStage_shouldStageRemoved() {
        Project project = new Project();
        Stage stage = new Stage();
        project.setStatus(ProjectStatus.IN_PROGRESS);
        when(teamMemberRepository.existsById(0L)).thenReturn(true);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));

        service.deleteStage(1L, 1L);

        verify(stageRepository).delete(stage);
    }

    @Test
    public void updateStage_userNotValid_shouldEntityNotFoundException() {
        StageUpdateDto dto = StageUpdateDto.builder()
                .countParticipant(1L)
                .teamRole(TeamRole.DEVELOPER)
                .build();

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.updateStage(dto, 1L));
    }

    @Test
    public void updateStage_stageNotExist_shouldEntityNotFoundException() {
        StageUpdateDto dto = StageUpdateDto.builder()
                .countParticipant(1L)
                .teamRole(TeamRole.DEVELOPER)
                .build();
        when(userContext.getUserId()).thenReturn(0L);
        when(teamMemberRepository.existsById(0L)).thenReturn(true);
        when(stageRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.updateStage(dto, 1L));
    }

    @Test
    public void updateStage_extraExecutors_shouldDataValidationException() {
        StageUpdateDto dto = StageUpdateDto.builder()
                .countParticipant(1L)
                .teamRole(TeamRole.DEVELOPER)
                .build();
        StageRoles stageRoles = StageRoles.builder()
                .teamRole(TeamRole.DESIGNER)
                .build();
        TeamMember teamMember = TeamMember
                .builder()
                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .build();
        Stage stage = Stage.builder()
                .stageRoles(new ArrayList<>(List.of(stageRoles)))
                .executors(new ArrayList<>(List.of(teamMember)))
                .build();

        when(userContext.getUserId()).thenReturn(0L);
        when(teamMemberRepository.existsById(0L)).thenReturn(true);
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));

        Assertions.assertThrows(DataValidationException.class,
                () -> service.updateStage(dto, 1L));
    }

    @Test
    public void updateStage_projectNotExist_shouldEntityNotFoundException() {
        StageUpdateDto dto = StageUpdateDto.builder()
                .countParticipant(1L)
                .teamRole(TeamRole.DEVELOPER)
                .build();
        StageRoles stageRoles = StageRoles.builder()
                .teamRole(TeamRole.DEVELOPER)
                .build();
        TeamMember teamMember = TeamMember
                .builder()
                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .build();
        Stage stage = Stage.builder()
                .stageRoles(new ArrayList<>(List.of(stageRoles)))
                .executors(new ArrayList<>(List.of(teamMember)))
                .project(Project.builder()
                        .id(1L).build())
                .build();

        when(userContext.getUserId()).thenReturn(0L);
        when(teamMemberRepository.existsById(0L)).thenReturn(true);
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.updateStage(dto, 1L));
    }

    @Test
    public void updateStage_wrongStatusProject_shouldForbiddenException() {
        StageUpdateDto dto = StageUpdateDto.builder()
                .countParticipant(1L)
                .teamRole(TeamRole.DEVELOPER)
                .build();
        StageRoles stageRoles = StageRoles.builder()
                .teamRole(TeamRole.DEVELOPER)
                .build();
        TeamMember teamMember = TeamMember
                .builder()
                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .build();
        Project project = Project.builder()
                .status(ProjectStatus.CANCELLED)
                .id(1L)
                .build();
        Stage stage = Stage.builder()
                .stageRoles(new ArrayList<>(List.of(stageRoles)))
                .executors(new ArrayList<>(List.of(teamMember)))
                .project(project)
                .build();

        when(userContext.getUserId()).thenReturn(0L);
        when(teamMemberRepository.existsById(0L)).thenReturn(true);
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Assertions.assertThrows(ForbiddenException.class,
                () -> service.updateStage(dto, 1L));
    }

    @Test
    public void updateStage_enoughParticipantsStage_shouldResponseDtoExecutorsStage() {
        StageUpdateDto dto = StageUpdateDto.builder()
                .countParticipant(1L)
                .teamRole(TeamRole.DEVELOPER)
                .build();
        StageRoles stageRoles = StageRoles.builder()
                .teamRole(TeamRole.DEVELOPER)
                .build();
        TeamMember teamMember = TeamMember
                .builder()
                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .userId(3L)
                .build();
        Project project = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .id(1L)
                .build();
        Stage stage = Stage.builder()
                .stageId(4L)
                .stageName("test")
                .stageRoles(new ArrayList<>(List.of(stageRoles)))
                .executors(new ArrayList<>(List.of(teamMember)))
                .project(project)
                .build();

        when(userContext.getUserId()).thenReturn(0L);
        when(teamMemberRepository.existsById(0L)).thenReturn(true);
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        StageDto responseDto = service.updateStage(dto, 1L);

        verify(stageInvitationService).sendInvitation(any(StageInvitationCreateDto.class));
        Assertions.assertEquals(teamMember.getUserId(), responseDto.teamMemberId().get(0));
    }

    @Test
    public void updateStage_invokeAnotherParticipantsStages_shouldResponseDtoHaveAnotherParticipants() {
        StageUpdateDto dto = StageUpdateDto.builder()
                .countParticipant(1L)
                .teamRole(TeamRole.DESIGNER)
                .build();
        StageRoles stageRoles = StageRoles.builder()
                .teamRole(TeamRole.DEVELOPER)
                .build();
        TeamMember teamMember = TeamMember
                .builder()
                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .userId(3L)
                .build();
        TeamMember anotherMember = TeamMember.builder()
                .userId(5L)
                .roles(new ArrayList<>(List.of(TeamRole.DESIGNER)))
                .build();
        Stage anotherStage = Stage.builder()
                .stageId(5L)
                .stageRoles(List.of(stageRoles))
                .executors(List.of(anotherMember))
                .build();
        Project project = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .stages(List.of(anotherStage))
                .id(1L)
                .build();
        Stage stage = Stage.builder()
                .stageId(4L)
                .stageName("test")
                .stageRoles(new ArrayList<>(List.of(stageRoles)))
                .executors(new ArrayList<>(List.of(teamMember)))
                .project(project)
                .build();


        when(userContext.getUserId()).thenReturn(0L);
        when(teamMemberRepository.existsById(0L)).thenReturn(true);
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        StageDto responseDto = service.updateStage(dto, 1L);

        verify(stageInvitationService).sendInvitation(any(StageInvitationCreateDto.class));
        Assertions.assertEquals(anotherMember.getUserId(), responseDto.teamMemberId().get(0));
    }

    @Test
    public void getStage_existProject_shouldThrowEntityNotFoundException() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.getStages(1L));
    }

    @Test
    public void getStage_responseDto_shouldResponseListStageDto() {
        Task task = Task.builder()
                .id(1L)
                .build();
        TeamMember teamMember = TeamMember.builder()
                .userId(2L)
                .build();
        Project project = Project.builder()
                .id(3L)
                .build();
        List<Stage> stageList = List.of(Stage.builder()
                .executors(List.of(teamMember))
                .tasks(List.of(task))
                .project(project)
                .build());
        project.setStages(stageList);

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        List<StageDto> responseDtoList = service.getStages(1L);
        StageDto responseDto = responseDtoList.get(0);
        Assertions.assertEquals(1L, responseDto.tasksId().get(0));
        Assertions.assertEquals(2L, responseDto.teamMemberId().get(0));
        Assertions.assertEquals(3L, responseDto.projectId());
    }


}