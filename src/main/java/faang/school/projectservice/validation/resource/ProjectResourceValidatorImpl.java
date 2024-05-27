package faang.school.projectservice.validation.resource;

import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.property.AmazonS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
public class ProjectResourceValidatorImpl implements ProjectResourceValidator {

    private final ResourceRepository resourceRepository;
    private final AmazonS3Properties amazonS3Properties;

    @Override
    public void validateMaxStorageSize(Project project, long newFileLength) {
        BigInteger newLength = project.getStorageSize().add(BigInteger.valueOf(newFileLength));
        if (newLength.compareTo(amazonS3Properties.getMaxFreeStorageSizeBytes()) > 0) {
            throw new DataValidationException("Project with projectId=" + project.getId() + " storage is full");
        }
    }

    @Override
    public void validateExistence(String key) {
        if (!resourceRepository.existsByKey(key)) {
            throw new DataValidationException("Resource with key=" + key + " not found");
        }
    }
}
