package faang.school.projectservice.validator;

import faang.school.projectservice.exception.resource.ResourceDeletedException;
import faang.school.projectservice.exception.resource.StorageLimitExceededException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component

public class ResourceValidator {
    public void checkIsDeletedResource(Resource resource) {
        if (resource.getStatus() == ResourceStatus.DELETED) {
            throw new ResourceDeletedException();
        }
    }

    public void checkStorageLimit(BigInteger newStorageSize, BigInteger maxStorageSize) {
        if (newStorageSize.compareTo(maxStorageSize) > 0) {
            throw new StorageLimitExceededException();
        }
    }
}
