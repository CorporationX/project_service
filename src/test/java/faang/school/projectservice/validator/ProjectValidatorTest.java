package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.projectservice.util.TestProject.PROJECT;
import static faang.school.projectservice.util.TestProject.PROJECT_ID;
import static faang.school.projectservice.util.TestProject.PROJECT_NAME;
import static faang.school.projectservice.util.TestUser.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {

    @Mock
    ProjectRepository projectRepository;

    @InjectMocks
    ProjectValidator projectValidator;

    @Test
    public void testIsProjectExists() {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(false);
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> projectValidator.exists(PROJECT_ID)
        );
        assertEquals("Entity project with projectId=" + PROJECT_ID + " not found.",
                entityNotFoundException.getMessage());
    }

    @Test
    public void testIsUniqOwnerAndName() {
        when(projectRepository.existsByOwnerUserIdAndName(USER_ID, PROJECT_NAME))
                .thenReturn(true);
        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> projectValidator.validateProjectIsUniqByIdAndName(PROJECT)
        );
        assertEquals("A project with the same owner and name exists.",
                dataValidationException.getMessage());
    }
}