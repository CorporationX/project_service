package faang.school.projectservice.validator;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static faang.school.projectservice.model.ProjectVisibility.PRIVATE;
import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTests {
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private ProjectValidator projectValidator;

    @Test
    void testValidateCreateProjectIBlank() {
        assertThrows(DataValidationException.class, () -> {
            projectValidator.validateCreateProject(ProjectDto.builder().name(" ").build());
        });
    }

    @Test
    void testValidateUpdateProject_ShouldThrowDataValidationException() {
        assertThrows(DataValidationException.class, () -> {
            projectValidator.validateUpdateProject( ProjectUpdateDto.builder()
                    .description("  ")
                    .status(null).build());
        });
    }

    @Test
    void testValidateFilter_ShouldThrowDataValidationException() {
        assertThrows(DataValidationException.class, () -> {
            projectValidator.validateFilter(ProjectFilterDto.builder()
                    .status(null)
                    .name("").build());
        });
    }

    @Test
    void testValidateProjectId_ShouldThrowDataValidationException() {
        assertThrows(DataValidationException.class, () -> {
            projectValidator.validateProjectId(0L);
        });
    }

    @Test
        // проверка работы приватности проекта
    void testValidateServiceGetProject_ShouldNotReturnProject() {
        var team = Team.builder().teamMembers(List.of(TeamMember.builder().id(1L).build())).build();
        var project = Project.builder().teams(List.of(team)).visibility(PRIVATE).build();
        assertNull(projectValidator.checkIfUserIsMember(2L, project));
    }

    @Test
    void testValidateServiceGetProject_ShouldReturnProject() {
        var project = Project.builder().visibility(PUBLIC).build();

        assertEquals(project, projectValidator.checkIfUserIsMember(2L, project));
    }

    @Test
    void testValidateServiceOwnerOfProject_ShouldThrowDataValidationException() {
        var project = Project.builder().id(1L).build();
        assertThrows(DataValidationException.class, () -> {
            projectValidator.checkForValidOwner(2L, project);
        });
    }
}
