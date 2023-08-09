package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage.StageStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.StageValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private StageRolesRepository stageRolesRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private StageMapper stageMapper;
    @Mock
    StageValidator stageValidator;
    @Mock
    TeamMember author;
    private StageDto stageDto;
    private Stage stage;
    private Stage stageWithStatusCreated;
    private Stage stageWithStatusIn_Progress;
    private String status;
    private Long stageId;
    private Long authorId;
    private Stage stageFromRepositoryWithWrongStatus;

    @BeforeEach
    void setUp() {
        stageId = 1L;
        authorId = 2L;
        status = "created";
        stageWithStatusCreated = Stage.builder()
                .stageId(1L)
                .status(StageStatus.CREATED)
                .build();
        stageWithStatusIn_Progress = Stage.builder()
                .stageId(2L)
                .status(StageStatus.IN_PROGRESS)
                .build();
        stageFromRepositoryWithWrongStatus = Stage.builder()
                .status(StageStatus.CANCELLED)
                .build();
        stage = Stage.builder()
                .stageName("stageName")
                .project(Project.builder().id(1L).build())
                .status(StageStatus.CREATED)
                .stageRoles(List.of(StageRoles.builder().teamRole(TeamRole.DEVELOPER).count(1).build()))
                .executors(List.of(author))
                .build();
        stageDto = StageDto.builder()
                .stageName("stageName")
                .projectId(1L)
                .status("CREATED")
                .stageRolesDto(List.of(StageRolesDto.builder().teamRole("OWNER").count(1).build()))
                .build();
        Project project = Project.builder()
                .id(1L)
                .teams(List.of(Team.builder()
                        .id(1L)
                        .teamMembers(List.of(TeamMember.builder()
                                .id(1L)
                                .team(Team.builder().id(1L).build())
                                .roles(List.of(TeamRole.MANAGER))
                                .build()))
                        .build()))
                .build();
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
        when(stageRepository.findAll()).thenReturn(new ArrayList<>(List.of(stageWithStatusCreated, stageWithStatusIn_Progress)));
        when(stageMapper.toDto(stageWithStatusCreated)).thenReturn(stageDto);

        stageService.getAllStagesByStatus(status);

        verify(stageRepository, times(1)).findAll();
        verify(stageMapper, times(1)).toDto(stageWithStatusCreated);
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

        when(stageRepository.getById(stageId)).thenReturn(stage);
        doNothing().when(stageValidator).isCompletedOrCancelled(any(Stage.class));
        when(teamMemberRepository.findById(authorId)).thenReturn(author);
        when(stageRepository.save(any(Stage.class))).thenReturn(stage);
        when(stageMapper.toDto(any(Stage.class))).thenReturn(stageDto);

        stageService.updateStage(stageDto, stageId, authorId);

        verify(stageRepository, times(1)).getById(stageId);
        verify(stageValidator, times(1)).isCompletedOrCancelled(any(Stage.class));
        verify(teamMemberRepository, times(1)).findById(authorId);
        verify(stageRepository, times(1)).save(any(Stage.class));
        verify(stageMapper, times(1)).toDto(stage);

        verifyNoMoreInteractions(stageRepository);
    }

    @Test()
    public void testUpdateStage_InvalidStage() {
        StageValidator stageValidator = new StageValidator();
        when(stageRepository.getById(stageId)).thenReturn(stageFromRepositoryWithWrongStatus);
        assertThrows(DataValidationException.class, () -> stageValidator.isCompletedOrCancelled(stageFromRepositoryWithWrongStatus), "Stage is completed or cancelled");
    }

    @Test
    void testMethodFindTeamMemberById_ThrowExceptionAndMessage() {
        when(teamMemberRepository.findById(authorId)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> teamMemberRepository.findById(authorId), String.format("Team member doesn't exist by id: %s", authorId));
    }
}