package faang.school.projectservice.validator.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class MomentValidatorTest {
    @InjectMocks
    private MomentValidator momentValidator;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MomentRepository momentRepository;

    private Project project;
    private MomentDto momentDto;
    private long id;

    @BeforeEach
    void setUp() {
        project = new Project();
        momentDto = MomentDto.builder().build();
        id = 1L;
    }

    @Test
    @DisplayName("Проверка на пустое поле name из dto")
    void givenEmptyNameWhenValidateMomentDtoReturnException() {
        assertThrows(DataValidationException.class, () ->
                momentValidator.validateMomentDto(momentDto));
    }

    @Test
    @DisplayName("Проверка на существующий проект")
    void givenEmptyNameWhenValidateProjectsByIdAndStatusReturnException() {
        Mockito.when(projectRepository.findAllByIds(Mockito.any()))
                .thenReturn(Collections.emptyList());
        assertThrows(DataValidationException.class, () ->
                momentValidator.validateProjectsByIdAndStatus(momentDto));
    }

    @Test
    @DisplayName("Проверка проекта на статут cancelled")
    void givenProjectStatusCancelledWhenValidateProjectsByIdAndStatusReturnException() {
        project.setStatus(ProjectStatus.CANCELLED);

        Mockito.when(projectRepository.findAllByIds(Mockito.any()))
                .thenReturn(Collections.singletonList(project));
        assertThrows(DataValidationException.class, () ->
                momentValidator.validateProjectsByIdAndStatus(momentDto));
    }

    @Test
    void givenValidWhenValidateProjectsByIdAndStatusReturnProject() {
        project.setStatus(ProjectStatus.IN_PROGRESS);

        Mockito.when(projectRepository.findAllByIds(Mockito.any()))
                .thenReturn(List.of(project));

        var result = momentValidator.validateProjectsByIdAndStatus(momentDto);
        assertEquals(List.of(project), result);
    }

    @Test
    @DisplayName("Проверка на существующий момент")
    void givenNotValidWhenValidateExistingMomentThenReturnException() {
        Mockito.when(momentRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () ->
                momentValidator.validateExistingMoment(id));
    }

    @Test
    @DisplayName("Проверка на существующий проект в котором есть пользователь с переданным ID")
    void givenNotValidWhenValidateProjectsByUserIdAndStatusReturnException() {
        Mockito.when(projectRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(DataValidationException.class, () ->
                momentValidator.validateProjectsByUserIdAndStatus(id));
    }

    @Test
    void givenValidWhenValidateProjectsByUserIdAndStatusReturnProject() {
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(1L);
        Team team = new Team();
        team.setTeamMembers(List.of(teamMember));
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setTeams(List.of(team));
        Mockito.when(projectRepository.findAll()).thenReturn(List.of(project));

        var result = momentValidator.validateProjectsByUserIdAndStatus(id);

        assertEquals(List.of(project), result);
    }
}