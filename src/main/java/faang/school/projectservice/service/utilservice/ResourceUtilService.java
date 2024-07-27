package faang.school.projectservice.service.utilservice;

import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceUtilService {

    private final ResourceRepository resourceRepository;

    public Resource save(Resource resource) {
        return resourceRepository.save(resource);
    }
}
