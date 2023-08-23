package faang.school.projectservice.service;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage_roles.StageRolesDto;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage.StageStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.StageValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class StageServiceTest {
    @InjectMocks
    private StageService stageService;
    @Mock
    private StageRepository stageRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private StageMapper stageMapper;
    @Spy
    private StageRolesMapper stageRolesMapper;
    @Mock
    private StageValidator stageValidator;

    @Mock
    private StageInvitationService stageInvitationService;
    @Mock
    private StageRolesRepository stageRolesRepository;
    @Mock
    private Stage stageMock;
    @Mock
    private Team teamMock;
    @Mock
    private TeamMember teamMemberMock;
    @Mock
    private Project projectMock;
    @Mock
    private StageRolesDto stageRolesDtoMock;
    @Captor
    private ArgumentCaptor<StageInvitationDto> captor;
    private StageDto stageDto;
    private Stage stage;
    private String status;
    private Long stageId;
    private Long authorId;
    private StageRolesDto stageRolesDto;
    private Project project;
    private TeamMember teamMember;

    @BeforeEach
    void setUp() {
        stageId = 1L;
        authorId = 2L;
        status = "created";
        stage = Stage.builder()
                .stageId(1L)
                .stageName("stageName")
                .project(Project.builder().id(1L).build())
                .status(StageStatus.CREATED)
                .stageRoles(List.of(StageRoles.builder().teamRole(TeamRole.DEVELOPER).count(1).build()))
                .executors(List.of(teamMemberMock))
                .build();
        stageDto = StageDto.builder()
                .stageName("stageName")
                .projectId(1L)
                .status("CREATED")
                .stageRolesDto(List.of(StageRolesDto.builder().teamRole(TeamRole.OWNER).count(1).build()))
                .build();
        project = Project.builder()
                .id(1L)
                .teams(List.of(Team.builder()
                        .id(1L)
                        .teamMembers(List.of(TeamMember.builder()
                                        .id(1L)
                                        .team(Team.builder().id(1L).build())
                                        .roles(List.of(TeamRole.MANAGER))
                                        .build(),
                                TeamMember.builder()
                                        .id(1L)
                                        .team(Team.builder().id(1L).build())
                                        .roles(List.of(TeamRole.DEVELOPER))
                                        .build()))
                        .build()))
                .build();
        stageRolesDto = StageRolesDto.builder().teamRole(TeamRole.DEVELOPER).count(2).build();
        teamMember = TeamMember.builder().id(1L).stages(List.of(stage)).build();
        when(projectRepository.getProjectById(1L)).thenReturn(project);
    }

    @Test
    void testMethodCreateStage() {
        when(stageMapper.toEntity(stageDto)).thenReturn(stage);
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        stageService.createStage(stageDto);

        verify(stageMapper, times(1)).toEntity(stageDto);
        verify(stageRepository, times(1)).save(stage);
        verify(stageMapper, times(1)).toDto(stage);
        verifyNoMoreInteractions(stageMapper);
        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    void testMethodGetAllStagesByStatus() {
        when(stageRepository.findAll()).thenReturn(new ArrayList<>(List.of(stage)));
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        stageService.getAllStagesByStatus(status);

        verify(stageRepository, times(1)).findAll();
        verify(stageMapper, times(1)).toDto(stage);
        verifyNoMoreInteractions(stageRepository);
        verifyNoMoreInteractions(stageMapper);
    }

    @Test
    void testMethodDeleteStagesById() {
        stageRepository.deleteById(stageId);

        verify(stageRepository, times(1)).deleteById(stageId);
        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    void testMethodGetAllStages() {
        when(stageRepository.findAll()).thenReturn((List.of(stage)));

        stageService.getAllStages();

        verify(stageRepository, times(1)).findAll();
        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    void testMethodGetStageById() {
        when(stageRepository.getById(stageId)).thenReturn(stage);

        stageService.getStageById(stageId);

        verify(stageRepository, times(1)).getById(stageId);
        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    void testMethodGetStageById_ThrowExceptionAndMessage() {
        when(stageRepository.getById(stageId)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> stageService.getStageById(stageId), "Stage not found by id: " + stageId);
    }

    @Test
//    @Disabled
    void testMethodUpdateStage() {
        List<Stage> stages = List.of(stageMock, stageMock);
        List<Team> teams = List.of(teamMock, teamMock);
        List<TeamMember> teamMembers = List.of(teamMemberMock, teamMemberMock);

        when(stageRepository.getById(stageId)).thenReturn(stageMock);
        doNothing().when(stageValidator).isCompletedOrCancelled(stageMock);
        when(stageRepository.save(stageMock)).thenReturn(stageMock);
        when(stageMock.getProject()).thenReturn(projectMock);
        when(projectMock.getId()).thenReturn(1L);
        when(projectMock.getTeams()).thenReturn(teams);
        when(projectRepository.getProjectById(1L)).thenReturn(projectMock);
        when(teamMock.getTeamMembers()).thenReturn(teamMembers);
        when(teamMemberMock.getStages()).thenReturn(stages);

        stageService.updateStage(stageRolesDto, stageId, authorId);

        verify(projectRepository, times(1)).getProjectById(1L);
        verify(stageRepository, times(1)).getById(stageId);
        verify(stageValidator, times(1)).isCompletedOrCancelled(stageMock);
        verify(stageRepository, times(1)).save(stageMock);

        verifyNoMoreInteractions(stageRepository);
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    public void testGetTeamMembers() {
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        List<TeamMember> teamMembers = stageService.getTeamMembers(1L);
        verify(projectRepository, times(1)).getProjectById(1L);
        assertEquals(2, teamMembers.size());
    }

    @Test
    public void testGetTeamRolesCount() {
        long count = stageService.getTeamRolesCount(stageRolesDto, stage);
        assertEquals(1, count);
    }

    @Test
    public void testSendStageInvitation() {
        StageInvitationDto stageInvitationDto = StageInvitationDto.builder()
                .stageId(stage.getStageId())
                .authorId(authorId)
                .invitedId(teamMember.getId())
                .build();

        stageService.sendStageInvitation(stage, authorId, teamMember);
        verify(stageInvitationService, times(1)).sendInvitation(stageInvitationDto);
    }

    @Test
    public void testSendStageInvitationArguments() {
        stageService.sendStageInvitation(stage, authorId, teamMember);
        verify(stageInvitationService).sendInvitation(captor.capture());

        StageInvitationDto capturedDto = captor.getValue();
        assertEquals(stage.getStageId(), capturedDto.getStageId());
        assertEquals(authorId, capturedDto.getAuthorId());
        assertEquals(teamMember.getId(), capturedDto.getInvitedId());
    }

    @Test
    public void testInviteTeamMemberToStage() {
        long countTeamRoles = 1L;
        List<Stage> stages = List.of(stageMock, stageMock);
        List<Team> teams = List.of(teamMock, teamMock);
        List<TeamMember> teamMembers = List.of(teamMemberMock, teamMemberMock);

        when(projectMock.getId()).thenReturn(1L);
        when(stageMock.getProject()).thenReturn(projectMock);
        when(projectMock.getTeams()).thenReturn(teams);
        when(teamMemberMock.getStages()).thenReturn(stages);
        when(teamMock.getTeamMembers()).thenReturn(teamMembers);
        when(stageRolesDtoMock.getCount()).thenReturn(5);
        when(projectRepository.getProjectById(1L)).thenReturn(projectMock);

        stageService.inviteTeamMemberToStage(stageMock, stageRolesDtoMock, authorId, countTeamRoles);

        verify(projectRepository, times(1))
                .getProjectById(1L);
    }

    @Test
    public void testUpdateStageRoles() {
        StageRoles stageRole1 = mock(StageRoles.class);
        StageRoles stageRole2 = mock(StageRoles.class);
        List<StageRoles> stageRoles = List.of(stageRole1, stageRole2);

        when(stageMock.getStageRoles()).thenReturn(stageRoles);
        when(stageRolesMapper.toEntity(stageRolesDtoMock)).thenReturn(new StageRoles());
        when(stageRolesRepository.save(stageRole1)).thenReturn(stageRole1);
        when(stageRepository.save(stageMock)).thenReturn(stageMock);

        stageService.updateStageRoles(stageRolesDto, stage);

        verify(stageRolesRepository, times(1)).save(any(StageRoles.class));
        verify(stageRolesRepository, times(1)).save(any(StageRoles.class));
        verify(stageRepository, times(1)).save(stage);
    }
}