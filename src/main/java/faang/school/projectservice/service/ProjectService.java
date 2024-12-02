package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public Project findProjectById(long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public ResponseProjectDto getProject(long projectId) {
        Project project = findProjectById(projectId);
        return projectMapper.toResponseDto(project);
    }

    public void updateStorageSize(BigInteger size, Project project) {
        project.setStorageSize(size);
        projectRepository.save(project);
    }
}
