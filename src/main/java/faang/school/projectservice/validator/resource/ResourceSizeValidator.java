package faang.school.projectservice.validator.resource;

import faang.school.projectservice.model.Project;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class ResourceSizeValidator {
    @Value("${file.storage.max-size}")
    long maxStorageSize;

    public void validateSize(BigInteger newStorageSize, BigInteger maxStorageSize) {
        if (newStorageSize.compareTo(maxStorageSize) > 0) {
            throw new RuntimeException("File size exceeds the maximum storage size of 2 GB");
        }
    }

    public void checkNullSizeOfProject(Project project) {
        if (project.getStorageSize() == null) {
            project.setStorageSize(BigInteger.ZERO);
        }
        if (project.getMaxStorageSize() == null) {
            project.setMaxStorageSize(BigInteger.valueOf(maxStorageSize));
        }
    }
}
