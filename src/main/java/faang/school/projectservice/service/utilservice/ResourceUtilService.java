package faang.school.projectservice.service.utilservice;

import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceUtilService {

    private final ResourceRepository resourceRepository;

    public Resource save(Resource resource) {
        return resourceRepository.save(resource);
    }

    public Resource getByIdAndProjectId(long id, long projectId) {
        return resourceRepository.findByIdAndProjectId(id, projectId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Resource id=%d refers to project id=%d not found",
                        id, projectId
                )));
    }

    public List<Resource> getAllByProjectId(long projectId) {
        return resourceRepository.findAllByProjectId(projectId);
    }
}
