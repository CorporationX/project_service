package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.dto.client.subproject.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
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
        assertDoesNotThrow(() -> validatorSubProjectController.isProjectDtoNull(projectDto));
    }

    @Test
    void testIsProjectDtoNull() {
        projectDto = null;

        NullValueException exception = assertThrows(NullValueException.class, () -> validatorSubProjectController.isProjectDtoNull(projectDto));
        assertEquals(exception.getMessage(), "ProjectDto is null");
    }

    @Test
    void testIsParentProjectNull() {
        projectDto.setParentProjectId(null);

        NullValueException exception = assertThrows(NullValueException.class, () -> validatorSubProjectController.isParentProjectNull(projectDto));
        assertEquals(exception.getMessage(), "ParentProject is null");
    }

    @Test
    void testIsParentProjectNotNull() {
        projectDto.setParentProjectId(new Project().getId());

        assertDoesNotThrow(() -> validatorSubProjectController.isParentProjectNull(projectDto));
    }

    @Test
    void testIsProjectNameNull() {
        NullValueException exception = assertThrows(NullValueException.class, () -> validatorSubProjectController.isProjectNameNull(projectDto));
        assertEquals(exception.getMessage(), "NameProject is null");
    }

    @Test
    void testIsProjectNameNotNull() {
        projectDto.setName("Some name");

        assertDoesNotThrow(() -> validatorSubProjectController.isProjectNameNull(projectDto));
    }

    @Test
    void testIsProjectStatusNull() {
        NullValueException exception = assertThrows(NullValueException.class, () -> validatorSubProjectController.isProjectStatusNull(projectDto));

        assertEquals(exception.getMessage(), "StatusProject is null");
    }

    @Test
    void testIsProjectStatusNotNull() {
        projectDto.setStatus(ProjectStatus.ON_HOLD);

        assertDoesNotThrow(() -> validatorSubProjectController.isProjectStatusNull(projectDto));
    }

    @Test
    void testIsProjectVisibilityNull() {
        NullValueException exception = assertThrows(NullValueException.class, () -> validatorSubProjectController.isProjectVisibilityNull(projectDto));

        assertEquals(exception.getMessage(), "VisibilityProject is null");
    }

    @Test
    void testIsProjectVisibilityNotNull() {
        projectDto.setVisibility(ProjectVisibility.PUBLIC);

        assertDoesNotThrow(() -> validatorSubProjectController.isProjectVisibilityNull(projectDto));
    }

    @Test
    void testIsProjectOwnerNull() {
        NullValueException exception = assertThrows(NullValueException.class, () -> validatorSubProjectController.isProjectOwnerNull(projectDto));

        assertEquals(exception.getMessage(), "OwnerProject is null");
    }

    @Test
    void testIsProjectOwnerNotNull() {
        projectDto.setOwnerId(10L);

        assertDoesNotThrow(() -> validatorSubProjectController.isProjectOwnerNull(projectDto));
    }
}