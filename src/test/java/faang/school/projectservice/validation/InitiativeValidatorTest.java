package faang.school.projectservice.validation;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InitiativeRepository;
import faang.school.projectservice.service.TeamMemberService;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class InitiativeValidatorTest {
    @Mock
    private InitiativeRepository initiativeRepository;

    @Mock
    private TeamMemberService teamMemberService;

    @InjectMocks
    private InitiativeValidator initiativeValidator;

    private Long projectId;
    private Long curatorId;
    private TeamMember teamMember;
    private Initiative initiative;

    @BeforeEach
    void setup(){
        projectId = 1L;
        curatorId = 1L;
        teamMember = new TeamMember();
        initiative = new Initiative();
    }


    @Test
    void testProjectHasNotActiveInitiativePositive() {
        Initiative inactiveInitiative = new Initiative();
        inactiveInitiative.setStatus(InitiativeStatus.DONE);

        when(initiativeRepository.findAllByProjectId(projectId)).thenReturn(List.of(inactiveInitiative));

        initiativeValidator.projectHasNotActiveInitiative(projectId);

        verify(initiativeRepository).findAllByProjectId(projectId);
    }

    @Test
    void testProjectHasNotActiveInitiativeNegative() {
        Initiative activeInitiative = new Initiative();
        activeInitiative.setStatus(InitiativeStatus.IN_PROGRESS);

        when(initiativeRepository.findAllByProjectId(projectId)).thenReturn(List.of(activeInitiative));

        assertThrows(DataValidationException.class, () -> initiativeValidator.projectHasNotActiveInitiative(projectId));
    }

    @Test
    void testCuratorRoleValidPositive() {
        teamMember.setRoles(List.of(TeamRole.ANALYST));

        when(teamMemberService.findById(curatorId)).thenReturn(teamMember);

        initiativeValidator.curatorRoleValid(curatorId);
    }

    @Test
    void testCuratorRoleValidNegative() {
        teamMember.setRoles(List.of(TeamRole.DEVELOPER));

        when(teamMemberService.findById(curatorId)).thenReturn(teamMember);

        assertThrows(DataValidationException.class, () -> initiativeValidator.curatorRoleValid(curatorId));
    }

    @Test
    void testCheckAllTasksDonePositive() {
        initiative.setStages(List.of(
                createStageWithTasks(TaskStatus.DONE, TaskStatus.DONE),
                createStageWithTasks(TaskStatus.DONE)
        ));

        initiativeValidator.checkAllTasksDone(initiative);
    }

    @Test
    void testCheckAllTasksDoneNegative() {
        initiative.setStages(List.of(
                createStageWithTasks(TaskStatus.DONE, TaskStatus.IN_PROGRESS),
                createStageWithTasks(TaskStatus.DONE)
        ));

        assertThrows(RuntimeException.class, () -> initiativeValidator.checkAllTasksDone(initiative));
    }

    private Stage createStageWithTasks(TaskStatus... statuses) {
        Stage stage = new Stage();
        List<Task> tasks = Stream.of(statuses).map(status -> {
            Task task = new Task();
            task.setStatus(status);
            return task;
        }).toList();
        stage.setTasks(tasks);
        return stage;
    }
}

