package faang.school.projectservice.validator;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.project.ProjectValidator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private ProjectValidator projectValidator;
    private final long USER_ID_WITH_ACCESS = 12L;

    @BeforeEach
    void init() {

    }

    @Test
    void validateToCreate_validDto_nothingHappens() {
        String projectName = "name";
        ProjectDto projectDto = ProjectDto
                .builder()
                .ownerId(USER_ID_WITH_ACCESS).name(projectName).description("desc")
                .build();

        mockUserContext();
        mockProjectNameExistenceCheck(false, USER_ID_WITH_ACCESS, projectName);

        projectValidator.validateToCreate(projectDto);
    }

    @Test
    void testValidateName_validName_nothingHappens() {
        String validName = "Faang shcool";
        projectValidator.validateName(validName);
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
    void testValidateDescription_validDesc_nothingHappens() {
        String validDesc1 = "some valid desc";
        String validDesc2 = null;
        projectValidator.validateDescription(validDesc1);
        projectValidator.validateDescription(validDesc2);
    }

    @Test
    void testValidateDescription_notValidDesc_ThrowsValidationException() {
        String notValidDesc1 = "   ";
        String notValidDesc2 = "";
        validateDescAssertThrowsValidationException(notValidDesc1);
        validateDescAssertThrowsValidationException(notValidDesc2);
    }

    private void validateNameAssertThrowsValidationException(String notValidName) {
        assertThrows(
                ValidationException.class,
                () -> projectValidator.validateName(notValidName)
        );
    }

    private void validateDescAssertThrowsValidationException(String notValidDesc) {
        assertThrows(
                ValidationException.class,
                () -> projectValidator.validateDescription(notValidDesc)
        );
    }

    @Test
    void testValidateAccessToProject_haveAccess_nothingHappens() {
        mockUserContext();
        projectValidator.validateAccessToProject(USER_ID_WITH_ACCESS);
    }

    @Test
    void testValidateAccessToProject_haveNotAccess_throwsSecurityException() {
        mockUserContext();
        long USER_WITH_NO_ACCESS = USER_ID_WITH_ACCESS + 1;
        assertThrows(
                SecurityException.class,
                () -> projectValidator.validateAccessToProject(USER_WITH_NO_ACCESS)
        );
    }

    @Test
    void testHaveAccessToProject_userIdNotEqualsContext_returnsFalse() {
        mockUserContext();
        boolean haveAccessToProject = projectValidator.haveAccessToProject(USER_ID_WITH_ACCESS + 1);
        assertFalse(haveAccessToProject);
    }

    @Test
    void testHaveAccessToProject_userIdEqualsContext_returnsTrue() {
        mockUserContext();
        boolean haveAccessToProject = projectValidator.haveAccessToProject(USER_ID_WITH_ACCESS);
        assertTrue(haveAccessToProject);
    }

    private void mockUserContext() {
        when(userContext.getUserId()).thenReturn(USER_ID_WITH_ACCESS);
    }

    @Test
    void testValidateNameExistence_nameExists_throwsValidationException() {
        long projectId = 10L;
        String projectName = "name";
        mockProjectNameExistenceCheck(true, projectId, projectName);
        when(projectRepository.existsByOwnerUserIdAndName(projectId, projectName)).thenReturn(true);
        assertThrows(
                ValidationException.class,
                () -> projectValidator.validateNameExistence(projectId, projectName)
        );
    }

    @Test
    void testValidateNameExistence_nameNotExist_nothingHappens() {
        long projectId = 5L;
        String projectName = "name";
        mockProjectNameExistenceCheck(false, projectId, projectName);
        when(projectRepository.existsByOwnerUserIdAndName(projectId, projectName)).thenReturn(false);
        projectValidator.validateNameExistence(projectId, projectName);
    }

    private void mockProjectNameExistenceCheck(boolean isExist, long userId, String projectName) {
        when(projectRepository.existsByOwnerUserIdAndName(userId, projectName)).thenReturn(isExist);
    }
}