package faang.school.projectservice.service.project;

<<<<<<< HEAD
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public boolean existsById(long projectId) {
        return projectRepository.existsById(projectId);

    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public boolean existsById(Long projectId) {
        return projectRepository.existsById(projectId);
    }
}
