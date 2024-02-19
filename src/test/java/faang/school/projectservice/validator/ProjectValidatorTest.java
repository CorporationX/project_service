package faang.school.projectservice.validator;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.project.ProjectValidator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private ProjectValidator projectValidator;
    private final long USER_WITH_ACCESS = 12L;

    @BeforeEach
    void init() {

    }

    @Test
    void validateToCreate() {

    }

    @Test
    void testValidateName_validName_nothingHappens() {
        String validName = "Faang shcool";
        projectValidator.validateName(validName);
    }

    private void validateNameAssertThrowsValidationException(String notValidNames) {
        assertThrows(
                ValidationException.class,
                () -> projectValidator.validateName(notValidNames)
        );
    }

    @Test
    void testValidateName_notValidName_throwsValidationException() {
        String notValidName1 = "   ";
        String notValidName2 = "";
        String notValidName3 = null;
        validateNameAssertThrowsValidationException(notValidName1);
        validateNameAssertThrowsValidationException(notValidName2);
        validateNameAssertThrowsValidationException(notValidName3);
    }

    @Test
    void testValidateAccessToProject_haveAccess_nothingHappens() {
        mockHaveAccessToProject();
        projectValidator.validateAccessToProject(USER_WITH_ACCESS);
    }

    @Test
    void testValidateAccessToProject_haveNotAccess_throwsSecurityException() {
        mockHaveAccessToProject();
        long USER_WITH_NO_ACCESS = USER_WITH_ACCESS + 1;
        assertThrows(
                SecurityException.class,
                () -> projectValidator.validateAccessToProject(USER_WITH_NO_ACCESS)
        );
    }

    private void mockHaveAccessToProject() {
        when(userContext.getUserId()).thenReturn(USER_WITH_ACCESS);
    }

    @Test
    void testValidateNameExistence_nameExists_throwsValidationException() {
        long userId = 10L;
        String projectName = "name";
        when(projectRepository.existsByOwnerUserIdAndName(userId, projectName)).thenReturn(true);
        assertThrows(
                ValidationException.class,
                () -> projectValidator.validateNameExistence(userId, projectName)
        );
    }

    @Test
    void testValidateNameExistence_nameNotExist_nothingHappens() {
        long userId = 10L;
        String projectName = "name";
        when(projectRepository.existsByOwnerUserIdAndName(userId, projectName)).thenReturn(false);
        projectValidator.validateNameExistence(userId, projectName);
    }
}