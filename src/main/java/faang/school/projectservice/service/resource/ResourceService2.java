package faang.school.projectservice.service.resource;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

@Service
@AllArgsConstructor
public class ResourceService2 {

    private final ResourceRepository resourceRepository;

    public Resource getResourceByKey(String key) {
        return resourceRepository.findByKey(key)
                .orElseThrow(() -> new EntityNotFoundException("Resource with key " + key + " doesn't exist"));
    }

    @Transactional
    public Resource putResource(String resourceKey, MultipartFile file, ResourceType resourceType) {
        Resource resource = new Resource();
        resource.setName(file.getName());
        resource.setKey(resourceKey);
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setType(resourceType);
        resource.setStatus(ResourceStatus.ACTIVE);

        return resourceRepository.save(resource);
    }

    @Transactional
    public Resource markResourceAsDeleted(String resourceKey) {
        Resource resource = getResourceByKey(resourceKey);
        resource.setStatus(ResourceStatus.DELETED);

        return resourceRepository.save(resource);
    }
}

