package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.stage.StageDTO;
import faang.school.projectservice.dto.client.stage.StageDtoCreate;
import faang.school.projectservice.mapper.StageCreateMapper;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static faang.school.projectservice.model.TeamRole.OWNER;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {
    @Mock
    private StageRepository stageRepository;
    @Mock
    private StageRolesRepository stageRolesRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private StageCreateMapper stageCreateMapper;
    @Mock
    private StageRolesMapper stageRolesMapper;
    @Mock
    private StageMapper stageMapper;

    @InjectMocks
    private StageService stageService;

    private StageDtoCreate stageDtoCreate;
    private StageDTO stageDTO;
    private Project project;
    private Stage stage;
    private List<StageRoles> stageRoles;

    @Test
    void PositiveCreate_ShouldCreateNewStage() {
        Long creatorId = 1L;
        Long projectId = 1L;
        project = getProject();
        stageDtoCreate = getStageDtoCreate();
        stage = getStage();
        stageRoles = getStageRoles();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(stageCreateMapper.toEntity(stageDtoCreate)).thenReturn(stage);
        when(stageRolesMapper.mapRolesToEntities(stageDtoCreate.getRoleAndCount(), stage)).thenReturn(stageRoles);
       //when(stageCreateMapper.toDto(stage)).thenReturn(StageDtoCreate.builder().build());
        when(stageMapper.toDto(stage)).thenReturn(
                StageDTO.builder()
                        .id(stage.getStageId())
                        .stageName(stage.getStageName())
                        .projectId(stage.getProject().getId())
                        .tasksIds(new ArrayList<>())
                        .stageRoleIds(stageRoles.stream().map(StageRoles::getId)
                                .filter(Objects::nonNull)
                                .collect(toList()))
                        .executorsIds(new ArrayList<>())
                        .build()
        );
        // When
        StageDTO result = stageService.create(stageDtoCreate, creatorId, projectId);
        // Then
        verify(projectRepository).findById(projectId);
        verify(stageCreateMapper).toEntity(stageDtoCreate);
        verify(stageRolesMapper).mapRolesToEntities(stageDtoCreate.getRoleAndCount(), stage);
        verify(stageMapper).toDto(stage);
        verify(stageRepository).save(stage);
        System.out.println(result);
        assertThat(result).isNotNull();

    }

    private StageDtoCreate getStageDtoCreate() {
        HashMap<TeamRole, Integer> teamRoles = new HashMap<>();
        teamRoles.put(OWNER, 1);
        stageDtoCreate =
                StageDtoCreate.builder()
                        .id(1L)
                        .stageName("Test")
                        .roleAndCount(teamRoles)
                        .build();
        return stageDtoCreate;
    }

    private Project getProject() {
        project = new Project();
        project.setId(1L);
        project.setName("test");
        project.setStages(new ArrayList<>());
        return project;
    }

    private Stage getStage() {
        stage = Stage.builder()
                .stageId(1L)
                .stageName("Test")
                .tasks(new ArrayList<>())
                .executors(new ArrayList<>())
                .project(project)
                .build();
        return stage;
    }

    private List<StageRoles> getStageRoles() {
        stageRoles = new ArrayList<>();
        StageRoles stageRole = new StageRoles();
        stageRole.setId(1L);
        stageRole.setTeamRole(OWNER);
        stageRole.setCount(1);
        stageRole.setStage(stage); // Устанавливаем stage для StageRoles
        stageRoles.add(stageRole);
        return stageRoles;
    }

}