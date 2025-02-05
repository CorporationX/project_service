package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.project.ProjectDto;
import org.springframework.http.ResponseEntity;

public interface ProjectService {
    ResponseEntity<ProjectDto> getProject(Long projectId);
}
