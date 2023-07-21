package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public List<ProjectDto> getAllProjects() {
        return projectMapper.toDtoList(projectRepository.findAll());
    }

    @Transactional
    public ProjectDto getProject(long projectId) {
        validationProjectExists(projectId);
        return projectMapper.toDto(projectRepository.getProjectById(projectId));
    }

    private void validationProjectExists(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new DataValidationException("This project doesn't exist");
        }
    }
}
