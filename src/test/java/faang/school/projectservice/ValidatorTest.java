package faang.school.projectservice;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidatorTest {

    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private ProjectValidator projectValidator;

    private ProjectDto projectDto = new ProjectDto();
    private Project project = new Project();
    private Long userId;
    private Team team = new Team();
    private TeamMember teamMember = new TeamMember();


    @BeforeEach
    void setUp() {
        projectDto = new ProjectDto();
        project = new Project();
        userId = 1L;

        projectDto.setId(1L);
        projectDto.setOwnerId(userId);
        projectDto.setName("first");

        project.setId(1L);
        project.setOwnerId(userId);
        project.setTeams(Collections.singletonList(team));

        teamMember.setUserId(1L);
        team.setTeamMembers(Collections.singletonList(teamMember));
    }

    @Test
    public void testCreateValidationThrowsException() {
        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                .thenReturn(true);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectValidator.checkIfProjectExists(projectDto));
        assertEquals(("Ошибка: пользователь id" + projectDto.getOwnerId() +
                " уже создал проект с названием " + projectDto.getName()), exception.getMessage());
    }

    @Test
    public void testCreateValidationNoException() {
        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                .thenReturn(false);
        assertDoesNotThrow(() -> projectValidator.checkIfProjectExists(projectDto));
    }

    @Test
    public void testUserValidatorThrowsException() {
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectValidator.userValidator(userId));
        assertEquals(("Ошибка: пользователь id" + userId + "не найден"), exception.getMessage());
    }

    @Test
    public void testUserValidatorNoException() {
        when(projectRepository.findAll()).thenReturn(Collections.singletonList(project));
        assertDoesNotThrow(() -> projectValidator.userValidator(userId));
    }
}
