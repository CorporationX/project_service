package faang.school.projectservice.service.resource;

import faang.school.projectservice.jpa.ProjectResourceRepository;
import faang.school.projectservice.model.ProjectResource;
import faang.school.projectservice.model.ResourceStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor
public class ProjectResourceDAO {
    private final ProjectResourceRepository projectResourceRepository;

    @Transactional
    public void setStatus(ProjectResource projectResource, ResourceStatus status) {
        projectResource.setStatus(status);
        projectResourceRepository.save(projectResource);
    }
}
