package faang.school.projectservice.validation;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validation.initiative.InitiativeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitiativeValidatorTest {
    @Mock
    private StageRepository stageRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @InjectMocks
    private InitiativeValidator validator;

    private InitiativeDto dto;
    private TeamMember curator;
    private List<Stage> stages;

    @BeforeEach
    void init() {
        Task task1 = Task.builder().status(TaskStatus.DONE).build();
        Task task2 = Task.builder().status(TaskStatus.DONE).build();
        Task task3 = Task.builder().status(TaskStatus.DONE).build();

        stages = List.of(
                Stage.builder().stageId(4L).tasks(List.of(task1)).build(),
                Stage.builder().stageId(5L).tasks(List.of(task2)).build(),
                Stage.builder().stageId(6L).tasks(List.of(task3)).build()
        );

        dto = InitiativeDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .curatorId(2L)
                .projectId(3L)
                .status(InitiativeStatus.IN_PROGRESS)
                .stageIds(List.of(4L, 5L, 6L))
                .build();

        Project project = Project.builder().id(3L).build();

        curator = TeamMember.builder()
                .id(8L)
                .userId(2L)
                .team(Team.builder().project(project).build())
                .roles(List.of(TeamRole.OWNER))
                .build();
    }

    @Test
    void validateNullProject() {
        dto.setProjectId(null);

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(dto));
        assertEquals("initiative projectId must not be null", e.getMessage());
    }

    @Test
    void validateNullName() {
        dto.setName(null);

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(dto));
        assertEquals("initiative name must not be null", e.getMessage());
    }

    @Test
    void validateBlankName() {
        dto.setName("  ");

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(dto));
        assertEquals("initiative name must not be null", e.getMessage());
    }

    @Test
    void validateNullDescription() {
        dto.setDescription(null);

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(dto));
        assertEquals("initiative description must not be null", e.getMessage());
    }

    @Test
    void validateBlankDescription() {
        dto.setDescription("   ");

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(dto));
        assertEquals("initiative description must not be null", e.getMessage());
    }

    @Test
    void validateNullCuratorId() {
        dto.setCuratorId(null);

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(dto));
        assertEquals("initiative curatorId must not be null", e.getMessage());
    }

    @Test
    void validateNullStatus() {
        dto.setStatus(null);

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(dto));
        assertEquals("initiative status must not be null", e.getMessage());
    }

    @Test
    void validate() {
        assertDoesNotThrow(() -> validator.validate(dto));
    }

    @Test
    void validateCuratorNotOwner() {
        curator.setRoles(List.of(TeamRole.INTERN));

        when(teamMemberRepository.findById(dto.getCuratorId())).thenReturn(curator);

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validateCurator(dto));
        assertEquals("curator must have owner role", e.getMessage());

        verify(teamMemberRepository, times(1)).findById(dto.getCuratorId());
    }

    @Test
    void validateCuratorNotInTheSameProject() {
        curator.getTeam().getProject().setId(1L);

        when(teamMemberRepository.findById(dto.getCuratorId())).thenReturn(curator);

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validateCurator(dto));
        assertEquals("curator not in the initiative project", e.getMessage());

        verify(teamMemberRepository, times(1)).findById(dto.getCuratorId());
    }

    @Test
    void validateCurator() {
        when(teamMemberRepository.findById(dto.getCuratorId())).thenReturn(curator);

        assertDoesNotThrow(() -> validator.validateCurator(dto));

        verify(teamMemberRepository, times(1)).findById(dto.getCuratorId());
    }

    @Test
    void validateClosedInitiativeNotAllClosed() {
        stages.get(0).getTasks().get(0).setStatus(TaskStatus.IN_PROGRESS);

        when(stageRepository.findAll()).thenReturn(stages);

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validateClosedInitiative(dto));
        assertEquals("All tasks in all stages must be done to close the initiative", e.getMessage());

        verify(stageRepository, times(1)).findAll();
    }

    @Test
    void validateClosedInitiative() {
        when(stageRepository.findAll()).thenReturn(stages);

        assertDoesNotThrow(() -> validator.validateClosedInitiative(dto));

        verify(stageRepository, times(1)).findAll();

    }
}