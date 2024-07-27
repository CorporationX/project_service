package faang.school.projectservice.validation;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InitiativeRepository;
import faang.school.projectservice.service.TeamMemberService;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

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

    @BeforeEach
    void setup() {
        curatorId = 1L;
        projectId = 1L;
    }

    @Test
    void testCheckProjectActiveInitiative() {
        Initiative activeInitiative = new Initiative();
        activeInitiative.setStatus(InitiativeStatus.ACCEPTED);
        Project project = new Project();
        project.setId(projectId);
        activeInitiative.setProject(project);
        Initiative closedInitiative = new Initiative();
        closedInitiative.setStatus(InitiativeStatus.CLOSED);
        closedInitiative.setProject(project);
        List<Initiative> initiatives = Arrays.asList(activeInitiative, closedInitiative);
        when(initiativeRepository.findAll()).thenReturn(initiatives);
        boolean result = initiativeValidator.checkProjectActiveInitiative(projectId);
        assertTrue(result);
    }

    @Test
    void testCheckCuratorRole() {
        TeamMember member = new TeamMember();
        member.setRoles(Arrays.asList(TeamRole.ANALYST, TeamRole.DEVELOPER));
        when(teamMemberService.findById(curatorId)).thenReturn(member);
        boolean result = initiativeValidator.checkCuratorRole(curatorId);
        assertTrue(result);
    }

    @Test
    void testCheckStagesStatusInitiative() {
        Initiative initiative = new Initiative();
        Stage stageFirst = new Stage();
        Task taskFirst = new Task();
        taskFirst.setStatus(TaskStatus.DONE);
        stageFirst.setTasks(List.of(taskFirst));
        Stage stageSecond = new Stage();
        Task taskSecond = new Task();
        taskSecond.setStatus(TaskStatus.DONE);
        stageSecond.setTasks(List.of(taskSecond));
        initiative.setStages(Arrays.asList(stageFirst, stageSecond));
        boolean result = initiativeValidator.checkStagesStatusInitiative(initiative);
        assertTrue(result);
    }

}