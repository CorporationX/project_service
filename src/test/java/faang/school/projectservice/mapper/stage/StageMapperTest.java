package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.dto.stageroles.StageRolesDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StageMapperTest {

    private static final long ID_ONE = 1L;
    private static final long ID_TWO = 2L;
    private static final int COUNT_ONE = 1;
    private static final int COUNT_TWO = 2;
    private static final int COUNT_THREE = 3;
    private static final String STRING = "Test";
    private final StageMapper stageMapper = Mappers.getMapper(StageMapper.class);

    @Nested
    class ToDtoTest {

        @Test
        @DisplayName("Успех маппинга список Stage в список StageDto")
        void whenStagesToDtosThenSuccess() {
            Stage stageOne = Stage.builder()
                    .stageId(ID_ONE)
                    .stageName(STRING)
                    .build();

            Stage stageTwo = Stage.builder()
                    .stageId(ID_TWO)
                    .stageName(STRING)
                    .build();

            List<Stage> stages = new ArrayList<>(List.of(stageOne, stageTwo));

            List<StageDto> result = stageMapper.toStageDtos(stages);

            assertNotNull(result);
            assertEquals(result.size(), COUNT_TWO);
        }

        @Test
        @DisplayName("Успех маппинга Stage в StageDto")
        void whenToDtoThenSuccess() {
            Project project = new Project();
            project.setId(ID_ONE);
            StageRoles stageRoles = new StageRoles();
            stageRoles.setId(ID_ONE);
            Task task = new Task();
            task.setId(ID_ONE);
            TeamMember teamMember = new TeamMember();
            teamMember.setId(ID_ONE);

            List<StageRoles> stageRolesList = new ArrayList<>();
            stageRolesList.add(stageRoles);
            List<Task> tasks = new ArrayList<>();
            tasks.add(task);
            List<TeamMember> executors = new ArrayList<>();
            executors.add(teamMember);

            Stage stage = Stage.builder()
                    .stageId(ID_ONE)
                    .stageName(STRING)
                    .project(project)
                    .stageRoles(stageRolesList)
                    .tasks(tasks)
                    .executors(executors)
                    .build();

            StageDto result = stageMapper.toStageDto(stage);

            assertNotNull(result);
            assertEquals(result.getStageId(), ID_ONE);
            assertEquals(result.getStageName(), STRING);
            assertEquals(result.getProject().getId(), COUNT_ONE);
            assertEquals(result.getStageRoles().size(), COUNT_ONE);
            assertEquals(result.getTasks().size(), COUNT_ONE);
            assertEquals(result.getExecutors().size(), COUNT_ONE);
        }
    }

    @Nested
    class ToEntityTest {

        @Test
        @DisplayName("Успех маппинга StageCreateDto в Stage")
        void whenStageCreateDtoToEntityThenSuccess() {
            StageRolesDto stageRolesDtoOne = StageRolesDto.builder()
                    .teamRole(TeamRole.DESIGNER)
                    .id(ID_ONE)
                    .count(COUNT_TWO)
                    .build();

            StageRolesDto stageRolesDtoTwo = StageRolesDto.builder()
                    .teamRole(TeamRole.ANALYST)
                    .id(ID_TWO)
                    .count(COUNT_THREE)
                    .build();

            StageCreateDto stageCreateDto = StageCreateDto.builder()
                    .stageName(STRING)
                    .stageRolesDtos(List.of(stageRolesDtoOne, stageRolesDtoTwo))
                    .build();

            Stage result = stageMapper.toStageEntity(stageCreateDto);

            assertNotNull(result);
            assertEquals(result.getStageName(), STRING);
            assertEquals(result.getStageRoles().size(), COUNT_TWO);
        }

        @Test
        @DisplayName("Успех маппинга stageUpdateDto в Stage")
        void whenStageUpdateDtoToEntityThenSuccess() {
            StageUpdateDto stageUpdateDto = StageUpdateDto.builder()
                    .stageName(STRING)
                    .build();

            Stage result = stageMapper.toStageEntity(stageUpdateDto);

            assertNotNull(result);
            assertEquals(result.getStageName(), STRING);
            assertNull(result.getProject());
            assertNull(result.getStageRoles());
            assertNull(result.getTasks());
            assertNull(result.getExecutors());
        }
    }
}