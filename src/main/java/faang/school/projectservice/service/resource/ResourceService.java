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
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public Resource getResourceById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource with id " + id + " doesn't exist"));
    }

    @Transactional
    public Resource putResource(MultipartFile file, ResourceType resourceType) {
        Resource resource = new Resource();
        resource.setName(file.getName());
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setType(resourceType);
        resource.setStatus(ResourceStatus.ACTIVE);

        return resourceRepository.save(resource);
    }

    @Transactional
    public Resource markResourceAsDeleted(Long resourceId) {
        Resource resource = getResourceById(resourceId);
        resource.setStatus(ResourceStatus.DELETED);

        return resourceRepository.save(resource);
    }
}

