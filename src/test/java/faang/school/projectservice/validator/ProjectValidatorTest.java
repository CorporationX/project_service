package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

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

    @Test
    void checkStorageSizeExceededFailTest() {
        BigInteger maxStorageSize = new BigInteger("1000");
        BigInteger newStorageSize = new BigInteger("2000");
        assertThrows(RuntimeException.class,
                () -> projectValidator.checkStorageSizeExceeded(maxStorageSize, newStorageSize));
    }

    @Test
    void checkStorageSizeExceededSuccessTest() {
        BigInteger maxStorageSize = new BigInteger("1000");
        BigInteger newStorageSize = new BigInteger("500");
        projectValidator.checkStorageSizeExceeded(maxStorageSize, newStorageSize);
    }
}