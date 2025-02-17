package faang.school.projectservice.service;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public Resource getResourceRefById(long id) {
        return resourceRepository.getReferenceById(id);
    }
}
