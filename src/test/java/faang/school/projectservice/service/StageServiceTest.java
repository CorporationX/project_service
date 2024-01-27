package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.StageValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static faang.school.projectservice.model.TeamRole.ANALYST;
import static faang.school.projectservice.model.TeamRole.DEVELOPER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {
    @Mock
    private StageRepository stageRepository;
    @Mock
    private StageMapper stageMapper;
    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private StageValidator stageValidator;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private StageInvitationRepository stageInvitationRepository;
    @InjectMocks
    private StageService stageService;

    Long stageId = 1L;

    @Test
    void testCreateStage_whenProjectIdExistAndStatusNotCancelled_thenSaveStageToBd() {
        //Arrange
        List<StageRolesDto> stageRolesDtos = List.of(
                StageRolesDto.builder()
                        .teamRole(ANALYST)
                        .count(3)
                        .build());
        List<StageRoles> stageRoles = List.of(
                StageRoles.builder()
                        .teamRole(ANALYST)
                        .count(3)
                        .build());
        StageDto stageDto = StageDto.builder()
                .stageName("Explore")
                .projectId(1L)
                .stageRolesDto(stageRolesDtos).build();
        Long projectId = stageDto.getProjectId();
        Stage stage = Stage.builder()
                .project(Project.builder().id(projectId).build())
                .stageName(stageDto.getStageName())
                .stageRoles(stageRoles)
                .build();
        when(stageMapper.toEntity(stageDto)).thenReturn(stage);

        //Act
        stageService.createStage(stageDto);

        //Assert
        assertAll(
                () -> verify(projectValidator, times(1)).validateExistProjectById(projectId),
                () -> verify(stageValidator, times(1)).validateStatusProject(projectId),
                () -> verify(stageMapper, times(1)).toEntity(stageDto),
                () -> verify(stageRepository, times(1)).save(stage)
        );
    }

    @Test
    void testDeleteStageById_whenStageIsExist_thenDeleteStage() {
        //Arrange
        Stage stage = new Stage();
        when(stageRepository.getById(stageId)).thenReturn(stage);

        //Act
        stageService.deleteStageById(stageId);

        //Assert
        verify(stageRepository, times(1)).getById(stageId);
        verify(stageRepository, times(1)).delete(stage);
    }

    @Test
    void testGetStagesById() {
        //Act
        stageService.getStagesById(stageId);
        //Assert
        verify(stageRepository, times(1)).getById(stageId);
    }

    @Test
    void testGetAllStageByFilter() {
        //Arrange
        StageFilter filterMock = Mockito.mock(StageFilter.class);
        List<StageFilter> stageFilters = List.of(filterMock);
        StageService stageServiceForFilter =
                new StageService(stageRepository, stageMapper, projectValidator, stageValidator, stageFilters,
                        taskRepository, projectRepository, stageInvitationRepository);
        List<Stage> stages = List.of(new Stage());
        StageFilterDto filter = StageFilterDto.builder().stageNamePattern("Test").build();
        when(stageRepository.findAll()).thenReturn(stages);

        //Act
        stageServiceForFilter.getAllStageByFilter(filter);

        //Assert
        verify(stageRepository, times(1)).findAll();
//        verify(stageMapper,times(1)).toDto((Stage) any());
    }

    @Test
    void testUpdateStage_whenLackOfTeamMembers_thenReturnStageDto() {
        // Arrange
        TeamMember teamMember1 = TeamMember.builder()
                .stages(List.of(new Stage()))
                .roles(List.of(ANALYST)).build();
        TeamMember teamMember2 = TeamMember.builder()
                .stages(List.of(new Stage()))
                .roles(List.of(DEVELOPER)).build();
        List<TeamMember> teamMembers = List.of(teamMember1, teamMember2);

        Project project = Project.builder()
                .id(stageId)
                .teams(List.of(new Team(null, teamMembers, new Project())))
                .build();

        Stage stage = new Stage();
        stage = Stage.builder()
                .stageRoles(List.of(
                        new StageRoles(null, ANALYST, 2, stage),
                        new StageRoles(null, DEVELOPER, 2, stage)
                ))
                .executors(List.of(
                        new TeamMember(null, 1L,
                                List.of(ANALYST, DEVELOPER), null, List.of(stage))
                ))
                .project(project).build();

        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(stageInvitationRepository.save((StageInvitation) any())).thenReturn(new StageInvitation());

        // Act
        StageDto stageDto = stageService.updateStage(stageId);

        // Assert
        assertAll(
                () -> verify(stageRepository, times(2)).getById((Long) any()),
                () -> verify(stageMapper, times(1)).toDto((Stage) any()),
                () -> verify(stageInvitationRepository, times(2))
                        .save((StageInvitation) any())
        );
    }
}