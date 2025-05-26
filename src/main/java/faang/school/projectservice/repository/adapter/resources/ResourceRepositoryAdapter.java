package faang.school.projectservice.repository.adapter.resources;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ResourceRepositoryAdapter {
    private final ResourceRepository resourceRepository;

    public List<Resource> getAllResourcesById(List<Long> resourceId) {
        if (resourceId == null) {
            return new ArrayList<>();
        }
        return resourceRepository.findAllById(resourceId);
    }
}
