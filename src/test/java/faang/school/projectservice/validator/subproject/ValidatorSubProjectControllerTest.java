package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.model.dto.ProjectDto;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.enums.ProjectStatus;
import faang.school.projectservice.model.enums.ProjectVisibility;
import faang.school.projectservice.validator.ValidatorSubProjectController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.ion.NullValueException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidatorSubProjectControllerTest {
    private ValidatorSubProjectController validatorSubProjectController = new ValidatorSubProjectController();
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        validatorSubProjectController = new ValidatorSubProjectController();
        projectDto = new ProjectDto();
    }

    @Test
    void testIsProjectDtoNotNull() {
        assertDoesNotThrow(() -> validatorSubProjectController.validateProjectDtoNotNull(projectDto));
    }

    @Test
    void testValidateProjectDtoNotNull() {
        projectDto = null;

        NullValueException exception = assertThrows(NullValueException.class, () -> validatorSubProjectController.validateProjectDtoNotNull(projectDto));
        assertEquals(exception.getMessage(), "ProjectDto is null");
    }

    @Test
    void testValidateParentProjectNull() {
        projectDto.setParentProjectId(null);

        NullValueException exception = assertThrows(NullValueException.class, () -> validatorSubProjectController.validateParentProjectNull(projectDto));
        assertEquals(exception.getMessage(), "ParentProject is null");
    }

    @Test
    void testIsParentProjectNotNull() {
        Project project = new Project();
        project.setId(1L);
        projectDto.setParentProjectId(project.getId());

        assertDoesNotThrow(() -> validatorSubProjectController.validateParentProjectNull(projectDto));
    }

    @Test
    void testValidateProjectNameNotNull() {
        NullValueException exception = assertThrows(NullValueException.class, () -> validatorSubProjectController.validateProjectNameNotNull(projectDto));
        assertEquals(exception.getMessage(), "NameProject is null");
    }

    @Test
    void testIsProjectNameNotNull() {
        projectDto.setName("Some name");

        assertDoesNotThrow(() -> validatorSubProjectController.validateProjectNameNotNull(projectDto));
    }

    @Test
    void testValidateProjectStatusNotNull() {
        NullValueException exception = assertThrows(NullValueException.class, () -> validatorSubProjectController.validateProjectStatusNotNull(projectDto));

        assertEquals(exception.getMessage(), "StatusProject is null");
    }

    @Test
    void testIsProjectStatusNotNull() {
        projectDto.setStatus(ProjectStatus.ON_HOLD);

        assertDoesNotThrow(() -> validatorSubProjectController.validateProjectStatusNotNull(projectDto));
    }

    @Test
    void testValidateProjectVisibilityNotNull() {
        NullValueException exception = assertThrows(NullValueException.class, () -> validatorSubProjectController.validateProjectVisibilityNotNull(projectDto));

        assertEquals(exception.getMessage(), "VisibilityProject is null");
    }

    @Test
    void testIsProjectVisibilityNotNull() {
        projectDto.setVisibility(ProjectVisibility.PUBLIC);

        assertDoesNotThrow(() -> validatorSubProjectController.validateProjectVisibilityNotNull(projectDto));
    }

    @Test
    void testValidateProjectOwnerNull() {
        NullValueException exception = assertThrows(NullValueException.class, () -> validatorSubProjectController.validateProjectOwnerNull(projectDto));

        assertEquals(exception.getMessage(), "OwnerProject is null");
    }

    @Test
    void testIsProjectOwnerNotNull() {
        projectDto.setOwnerId(10L);

        assertDoesNotThrow(() -> validatorSubProjectController.validateProjectOwnerNull(projectDto));
    }
}