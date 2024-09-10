package faang.school.projectservice.validator.resource;

import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResourceSizeValidatorTest {
    @InjectMocks
    private ResourceSizeValidator resourceSizeValidator;

    @BeforeEach
    void setUp() {
        resourceSizeValidator.maxStorageSize = 2L * 1024 * 1024 * 1024;
    }

    @Test
    void validateSizeTestThrowsException() {
        BigInteger newStorageSize = BigInteger.valueOf(3L * 1024 * 1024 * 1024);
        BigInteger maxSize = BigInteger.valueOf(2L * 1024 * 1024 * 1024);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> resourceSizeValidator.validateSize(newStorageSize, maxSize));

        assertEquals("File size exceeds the maximum storage size of 2 GB", exception.getMessage());
    }

    @Test
    void validateSizeTestSuccess() {
        BigInteger newStorageSize = BigInteger.valueOf((long) 1024 * 1024 * 1024); // 1 GB
        BigInteger maxSize = BigInteger.valueOf(2L * 1024 * 1024 * 1024); // 2 GB

        assertDoesNotThrow(() -> resourceSizeValidator.validateSize(newStorageSize, maxSize));
    }

    @Test
    void checkNullSizeOfProjectTest_ShouldSetStorageSizeToZero_WhenNull() {
        Project project = new Project();
        project.setStorageSize(null);
        project.setMaxStorageSize(null);

        resourceSizeValidator.checkNullSizeOfProject(project);

        assertEquals(BigInteger.ZERO, project.getStorageSize());

        assertEquals(BigInteger.valueOf(2L * 1024 * 1024 * 1024), project.getMaxStorageSize());
    }

    @Test
    void checkNullSizeOfProjectTest_ShouldNotChangeStorageSize_WhenNotNull() {
        Project project = new Project();
        project.setStorageSize(BigInteger.valueOf(500L));
        project.setMaxStorageSize(BigInteger.valueOf(1024L));

        resourceSizeValidator.checkNullSizeOfProject(project);

        assertEquals(BigInteger.valueOf(500L), project.getStorageSize());

        assertEquals(BigInteger.valueOf(1024L), project.getMaxStorageSize());
    }
}

