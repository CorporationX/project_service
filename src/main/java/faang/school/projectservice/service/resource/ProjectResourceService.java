package faang.school.projectservice.service.resource;

import faang.school.projectservice.jpa.ProjectResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectResource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Slf4j
@Component
@AllArgsConstructor
public class ProjectResourceService {
    private final ProjectResourceRepository projectResourceRepository;

    @Transactional
    public void setStatus(ProjectResource projectResource, ResourceStatus status) {
        projectResource.setStatus(status);
        projectResourceRepository.save(projectResource);
    }

    @Transactional
    public void save(ProjectResource projectResource) {
        projectResourceRepository.save(projectResource);
    }

    @Transactional(readOnly = true)
    public ProjectResource findProjectResourceByIds(Long resourceId, Long projectId) {
        return projectResourceRepository.findResourceWithProject(resourceId, projectId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Resource with id: %d not found in project with id: %d", resourceId, projectId)));
    }

    public ProjectResource getProjectResource(Project project, TeamMember teamMember, String path, String fileName, BigInteger size, String type) {
        return ProjectResource.builder()
                .key(path)
                .name(generateResourceName(fileName, project.getName()))
                .size(size)
                .type(type)
                .status(ResourceStatus.PENDING)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .project(project)
                .build();
    }

    private String generateResourceName(String fileName, String entityName) {
        return entityName + " " + fileName;
    }
}
