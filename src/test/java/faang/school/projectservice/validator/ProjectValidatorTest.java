package faang.school.projectservice.validator;

import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
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
    @InjectMocks
    private ProjectValidator projectValidator;
    Long projectId = 1L;

    @Test
    void testValidateExistProjectById_whenNotExist_thenThrowDataValidationException() {
        // Arrange
        when(projectRepository.existsById(projectId)).thenReturn(false);

        // Act & Assert
        assertThrows(DataValidationException.class, () -> projectValidator.existsById(projectId));
    }
}