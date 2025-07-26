package faang.school.projectservice.util.task;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест для определения, состоит ли пользователь в команде проекта")
public class TaskUtilTest {
    @Mock
    private ProjectRepository repository;
    @Mock
    private UserContext userContext;
    @InjectMocks
    TaskUtil taskUtil;

    @Test
    @DisplayName("Проверка успешного сценария на принадлежность к команде проекта")
    void isInTeamTest() {
        Long projectId = 5L;

        TeamMember member1 = TeamMember.builder()
                .id(1L)
                .build();
        TeamMember member2 = TeamMember.builder()
                .id(2L)
                .build();
        TeamMember member3 = TeamMember.builder()
                .id(3L)
                .build();

        Team team = Team.builder()
                .teamMembers(List.of(member1, member2, member3))
                .build();

        Project project = Project.builder()
                .teams(List.of(team))
                .build();

        when(repository.findById(projectId)).thenReturn(Optional.of(project));
        when(userContext.getUserId()).thenReturn(1L);

        assertTrue(taskUtil.isInTeam(projectId));
    }

    @Test
    @DisplayName("Проверка передачи id пользователя, не состоящего в команде проекта")
    void isNotInTeam() {
        Long projectId = 5L;

        TeamMember member1 = TeamMember.builder()
                .id(1L)
                .build();
        TeamMember member2 = TeamMember.builder()
                .id(2L)
                .build();
        TeamMember member3 = TeamMember.builder()
                .id(3L)
                .build();

        Team team = Team.builder()
                .teamMembers(List.of(member1, member2, member3))
                .build();

        Project project = Project.builder()
                .teams(List.of(team))
                .build();

        when(repository.findById(projectId)).thenReturn(Optional.of(project));
        when(userContext.getUserId()).thenReturn(500L);

        assertThrows(ForbiddenException.class, () -> taskUtil.isInTeam(projectId));
    }

    @Test
    @DisplayName("Проверка отсутствия проекта с переданным id")
    void isNonExistentProjectId() {
        Long projectId = 5L;

        when(repository.findById(projectId)).thenReturn(Optional.ofNullable(null));

        assertThrows(EntityNotFoundException.class, () -> taskUtil.isInTeam(projectId));
    }
}
