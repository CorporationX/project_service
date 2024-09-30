package faang.school.projectservice.service.resource;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

@Service
@AllArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public Resource getResourceById(String resourceId) {
        return resourceRepository.findResourceByKey(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource with id " + resourceId + " doesn't exist"));
    }

    public Resource putResource(MultipartFile file, ResourceType resourceType, String key) {
        Resource resource = new Resource();
        resource.setName(file.getName());
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setType(resourceType);
        resource.setStatus(ResourceStatus.ACTIVE);

        return resourceRepository.save(resource);
    }

    public Resource markResourceAsDeleted(String resourceId) {
        Resource resource = getResourceById(resourceId);
        resource.setStatus(ResourceStatus.DELETED);

        return resourceRepository.save(resource);
    }
}

