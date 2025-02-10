package faang.school.projectservice;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StageMapperTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private StageRolesRepository stageRolesRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private StageMapper stageMapper = new StageMapperImpl();

    private StageDto stageDto;
    private Stage stage;

    @BeforeEach
    void setUp() {
        // Setup test DTO
        stageDto = StageDto.builder()
                .taskIds(List.of(1L, 2L))
                .stageRolesIds(List.of(3L, 4L))
                .executorsId(List.of(5L, 6L))
                .projectId(7L)
                .build();

        // Setup test entity
        stage = Stage.builder()
                .tasks(List.of(
                        Task.builder().id(1L).build(),
                        Task.builder().id(2L).build()))
                .stageRoles(List.of(
                        StageRoles.builder().id(3L).build(),
                        StageRoles.builder().id(4L).build()))
                .executors(List.of(
                        TeamMember.builder().id(5L).build(),
                        TeamMember.builder().id(6L).build()))
                .project(Project.builder().id(7L).build())
                .build();
    }

    @Test
    void testToEntity() {
        // Mock repository responses
        Mockito.when(taskRepository.getReferenceById(1L)).thenReturn(stage.getTasks().get(0));
        Mockito.when(taskRepository.getReferenceById(2L)).thenReturn(stage.getTasks().get(1));
        Mockito.when(stageRolesRepository.getReferenceById(3L)).thenReturn(stage.getStageRoles().get(0));
        Mockito.when(stageRolesRepository.getReferenceById(4L)).thenReturn(stage.getStageRoles().get(1));
        Mockito.when(teamMemberRepository.getReferenceById(5L)).thenReturn(stage.getExecutors().get(0));
        Mockito.when(teamMemberRepository.getReferenceById(6L)).thenReturn(stage.getExecutors().get(1));
        Mockito.when(projectRepository.getReferenceById(7L)).thenReturn(stage.getProject());

        // Execute mapping
        Stage result = stageMapper.toEntity(
                stageDto,
                taskRepository,
                stageRolesRepository,
                teamMemberRepository,
                projectRepository
        );

        // Verify results
        assertThat(result).isNotNull();
        assertThat(result.getTasks())
                .hasSize(2)
                .extracting(Task::getId)
                .containsExactly(1L, 2L);
        assertThat(result.getStageRoles())
                .hasSize(2)
                .extracting(StageRoles::getId)
                .containsExactly(3L, 4L);
        assertThat(result.getExecutors())
                .hasSize(2)
                .extracting(TeamMember::getId)
                .containsExactly(5L, 6L);
        assertThat(result.getProject())
                .isNotNull()
                .extracting(Project::getId)
                .isEqualTo(7L);
    }

    @Test
    void testToDto() {
        // Execute mapping
        StageDto result = stageMapper.toDto(
                stage,
                taskRepository,
                stageRolesRepository,
                teamMemberRepository
        );

        // Verify results
        assertThat(result).isNotNull();
        assertThat(result.getTaskIds()).containsExactly(1L, 2L);
        assertThat(result.getStageRolesIds()).containsExactly(3L, 4L);
        assertThat(result.getExecutorsId()).containsExactly(5L, 6L);
        assertThat(result.getProjectId()).isEqualTo(7L);
    }

    @Test
    void testToStageUpdateDto() {
        StageDto stageDto = StageDto.builder()
                .stageName("Test Stage")
                .build();

        StageUpdateDto result = stageMapper.toStageUpdateDto(stageDto);

        assertThat(result)
                .isNotNull()
                .extracting(StageUpdateDto::getStageName);
    }

    @Test
    void testToStageDto() {
        StageUpdateDto updateDto = StageUpdateDto.builder()
                .stageName("Updated Stage")
                .build();

        StageDto result = stageMapper.toStageDto(updateDto);

        assertThat(result)
                .isNotNull()
                .extracting(StageDto::getStageName);

    }
}