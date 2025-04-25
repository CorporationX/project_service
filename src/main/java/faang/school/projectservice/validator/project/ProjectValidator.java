package faang.school.projectservice.validator.project;

import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class ProjectValidator {

    private static final long FILE_MAX_COUNT_IN_PROJECT_GALLERY = 50;

    public void validateProjectStorageSize(BigInteger newStorageSize, Project project, BigInteger fileSize) {

        if(newStorageSize.compareTo(project.getMaxStorageSize()) > 0 ) {
            throw new IllegalArgumentException(String.format("Loading is impossible: the maximum size of the storage %d, file size %d",
                    project.getMaxStorageSize(), fileSize));
        }

        if (project.getResources().size() >= FILE_MAX_COUNT_IN_PROJECT_GALLERY) {
            throw new IllegalArgumentException(String.format("The file cannot be added to the storage, " +
                    "since the maximum limit in %d files is exceeded", FILE_MAX_COUNT_IN_PROJECT_GALLERY));
        }
    }
}
